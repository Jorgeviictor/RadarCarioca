# Radar Carioca

Copiloto de segurança e lucratividade para motoristas de aplicativo no Rio de Janeiro.

O app monitora ofertas de corrida em tempo real (Uber, 99) via AccessibilityService, analisa o destino contra um mapa geoespacial de risco e calcula a lucratividade líquida — tudo em menos de 1.500 ms, antes do motorista aceitar ou recusar.

---

## Arquitetura

```
Clean Architecture + MVVM + Hilt DI
```

```
domain/          ← UseCases, interfaces de Repository e Service (zero dependência Android)
data/            ← Implementações: Room (local), Firebase (remoto), DataStore (prefs)
di/              ← Módulos Hilt especializados
ui/              ← Jetpack Compose + ViewModels
service/         ← ForegroundService, AccessibilityService, BootReceiver
```

**Módulos Hilt:**

| Módulo | Responsabilidade |
|---|---|
| `DatabaseModule` | Room Database + DAOs |
| `RepositoryModule` | Bindings Interface → Impl |
| `ServiceModule` | RadarController, PermissionsChecker |
| `AlertModule` | Firebase, IoDispatcher, DataSources |
| `AuthModule` | FirebaseAuth, Firestore |

**22 Use Cases** — toda a lógica de negócio fica no domínio, isolada de Android.

---

## Pré-requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK API 29+
- Dispositivo físico recomendado (AccessibilityService não detecta apps reais no emulador)

---

## Setup

### 1. Clonar e importar

```bash
git clone https://github.com/Jorgeviictor/RadarCarioca.git
```

Abra o Android Studio → **File → Open** → selecione a pasta `RadarCarioca`.

### 2. Configurar chaves (local.properties)

```properties
MAPS_API_KEY=sua_chave_google_maps
ADMIN_MASTER_UID=uid_firebase_do_admin
GOOGLE_WEB_CLIENT_ID=seu_web_client_id
```

### 3. Firebase

1. Crie um projeto em [console.firebase.google.com](https://console.firebase.google.com)
2. Adicione app Android com package `com.radarcarioca`
3. Baixe `google-services.json` e coloque em `app/`

### 4. GeoJSON

O arquivo `app/src/main/assets/mapa_faccoes_rj.geojson` contém as áreas de risco.
Substitua pelo arquivo completo se disponível — as features precisam de `name` e `description`.

---

## Rodar o app

### Emulador

```
Run → Run 'app'
```

Toda a lógica financeira e GeoJSON funciona. AccessibilityService requer device físico.

### Dispositivo físico

1. Ative **Modo Desenvolvedor** → **Depuração USB**
2. Conecte o dispositivo
3. Conceda as permissões pelo Onboarding do app:
   - Acessibilidade → Radar Carioca → Ativar
   - Aparecer sobre outros apps
   - Bateria → Sem restrições

---

## Testes

### Estrutura

| Pasta | Tipo | Execução |
|---|---|---|
| `src/test/` | Unitários (JVM puro) | Sem emulador, sem device |
| `src/androidTest/` | Instrumentados | Requer emulador ou device |

**Regra:** Use `test/` para tudo que envolve domínio e lógica de negócio. Use `androidTest/` apenas para testes que precisam de Context, Room in-memory com Android Runner, ou Compose UI.

### Rodar os testes unitários

```bash
./gradlew test
```

Ou no Android Studio: clique com botão direito em `src/test/` → **Run Tests**.

### Bibliotecas de teste

| Biblioteca | Uso |
|---|---|
| JUnit 4 | Framework base de testes |
| `kotlinx-coroutines-test` | `runTest` para suspend functions e Flows |
| MockK | Mocks e spies para interfaces Kotlin |
| Turbine | Testar `Flow<T>` de forma declarativa |

### Cobertura atual

| Arquivo de teste | O que testa |
|---|---|
| `HelloWorldTest` | Smoke test — valida o ambiente JUnit |
| `GetActiveAlertsUseCaseTest` | Alertas ativos, filtro de inativos, emissão reativa |
| `GetNearbyAlertsUseCaseTest` | Raio de busca, alertas fora do alcance, validação de parâmetros |
| `RefreshAlertsUseCaseTest` | Refresh com sucesso, falha de rede, sem propagação de exceção |
| `ToggleRadarUseCaseTest` | Ativar/desativar radar com MockK — permissões, boot state |
| `ProcessRideOfferKeywordTest` | Fallback offline por palavra-chave (Maré, Alemão, Chapadão...) |
| `CalculateRideProfitUseCaseTest` | Cálculo financeiro: lucro, deadhead, margem, validações |
| `RideHistoryRepositoryTest` | Lucro total, contagens, remoção de registros antigos |

### Exemplo de teste com Turbine

```kotlin
@Test
fun `emite novo alerta adicionado em tempo real`() = runTest {
    useCase().test {
        val primeiro = awaitItem() as DataResult.Success
        assertEquals(0, primeiro.data.size)

        repository.add(fakeSecurityAlert(id = "novo"))

        val segundo = awaitItem() as DataResult.Success
        assertEquals(1, segundo.data.size)
        cancelAndIgnoreRemainingEvents()
    }
}
```

### Exemplo de teste com MockK

```kotlin
@Test
fun `ativar radar sem acessibilidade retorna false`() = runTest {
    every { permissionsChecker.getSystemStatus() } returns accessibilityMissing

    val result = useCase(currentlyActive = false)

    assertFalse(result)
    coVerify(exactly = 0) { driverSettingsRepository.setRadarEnabled(any()) }
}
```

---

## Fluxo de execução

```
Uber/99 exibe oferta
        ↓
RadarAccessibilityService captura texto (< 200ms)
        ↓
GeocodingService → lat/lng  (< 800ms online | instantâneo offline via keyword)
        ↓
CheckRideSafetyUseCase → GeoJSON local
        ↓
CalculateRideProfitUseCase → R$/KM, lucro líquido
        ↓
OverlayManager → card flutuante (VERDE / AMARELO / VERMELHO)
        ↓
TOTAL < 1.500ms
```

---

## Commits semânticos sugeridos

```bash
# Fase 1 — DI
git commit -m "refactor(di): remove AppModule legado substituído por módulos especializados"

# Fase 2 — Setup de testes
git commit -m "test(config): adiciona MockK e Turbine ao build.gradle"
git commit -m "test(smoke): cria HelloWorldTest para validar ambiente JUnit"

# Fase 3 — Testes de UseCase
git commit -m "test(alerts): cobre GetActiveAlertsUseCase e GetNearbyAlertsUseCase com Turbine"
git commit -m "test(alerts): cobre RefreshAlertsUseCase — sucesso, erro de rede e segurança"
git commit -m "test(radar): cobre ToggleRadarUseCase com MockK — permissões e boot state"
git commit -m "test(geo): cobre fallback offline por palavra-chave do ProcessRideOfferUseCase"

# Fase 4 — Documentação
git commit -m "docs: atualiza README com arquitetura, setup e guia completo de testes"
```

---

## Diário de Bordo

### O que foi feito

- **Limpeza de DI:** removido `AppModule.kt` legado (arquivo sem bindings, substituído por módulos especializados)
- **Setup de testes:** adicionados MockK 1.13.12 e Turbine 1.1.0 ao `build.gradle.kts`
- **8 arquivos de teste** cobrindo domínio completo: alertas, radar, financeiro, histórico e segurança geográfica

### Maior dificuldade técnica

**Testar Flows reativos com coroutines** — o padrão `flow.collect {}` trava o teste indefinidamente quando o Flow não completa (como `StateFlow` e `MutableStateFlow`).

**Solução:** Turbine resolve com `flow.test { awaitItem(); cancelAndIgnoreRemainingEvents() }` — permite consumir emissões declarativamente e cancelar o coletor sem deadlock.

### O que foi aprendido

- `@Binds` é preferível a `@Provides` para bindings interface→impl: gera menos código e falha em compile-time
- Separar `test/` de `androidTest/` não é só convenção — é uma decisão arquitetural que force a manter o domínio livre de dependências Android
- MockK com `relaxed = true` elimina boilerplate de stub para dependências que não são o foco do teste

---

*Radar Carioca — Seu copiloto nas ruas do Rio*
