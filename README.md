# Radar Carioca — Android Studio Setup

## Pré-requisitos
- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK API 29+
- Dispositivo ou emulador Android 10+

---

## 1. Importar o projeto

1. Abra o Android Studio
2. **File → Open** → selecione a pasta `RadarCarioca`
3. Aguarde o Gradle Sync (pode demorar na primeira vez — baixa ~200MB de dependências)

---

## 2. Configurar o Token do Mapbox (OBRIGATÓRIO)

O Mapbox requer um access token para funcionar.

### Passo 1: Criar conta gratuita
Acesse https://account.mapbox.com e crie uma conta (plano free tem 100.000 geocodificações/mês grátis).

### Passo 2: Adicionar o token
Edite `app/build.gradle.kts` e substitua:
```
"pk.SEU_TOKEN_MAPBOX_AQUI"
```
pelo seu token real:
```
"pk.eyJ1IjoiXXXXX..."
```

### Alternativa sem Mapbox (modo offline total)
O app funciona mesmo sem o token — usa o fallback de coordenadas hardcoded no `GeocodingService.kt`.
O alerta por keywords no texto (ex: "Chapadão", "Maré") também funciona sem geocodificação.

---

## 3. Substituir o GeoJSON (RECOMENDADO)

O arquivo `app/src/main/assets/mapa_faccoes_rj.geojson` tem apenas 17 features de exemplo.

Para usar seu arquivo completo com 3.369 features:
1. Renomeie seu arquivo para `mapa_faccoes_rj.geojson`
2. Substitua o arquivo em `app/src/main/assets/`
3. Certifique-se que as features têm as propriedades `name` e `description`

---

## 4. Configurar Firebase (OPCIONAL — para atualizações remotas)

Para ativar atualizações do GeoJSON sem publicar nova versão:

1. Acesse https://console.firebase.google.com
2. Crie um projeto "radar-carioca"
3. Adicione um app Android com package `com.radarcarioca`
4. Baixe o `google-services.json` e coloque em `app/`
5. Descomente a linha no `app/build.gradle.kts`:
   ```kotlin
   // id("com.google.gms.google-services") version "4.4.2"
   ```

---

## 5. Rodar o app

### No emulador (limitações):
- AccessibilityService não detecta apps reais (precisa de device físico)
- Overlay funciona normalmente
- Toda a lógica financeira e GeoJSON funciona

### No dispositivo físico (recomendado):
1. Ative **Modo Desenvolvedor** no Android
2. Ative **Depuração USB**
3. Conecte o dispositivo
4. No Android Studio: **Run → Run 'app'**

### Permissões a conceder no device:
O app guia o usuário pelo Onboarding, mas caso precise manualmente:
- **Acessibilidade**: Configurações → Acessibilidade → Radar Carioca → Ativar
- **Overlay**: Configurações → Apps → Radar Carioca → Aparecer sobre outros apps
- **Bateria**: Configurações → Apps → Radar Carioca → Bateria → Sem restrições

---

## Estrutura do Projeto

```
app/src/main/java/com/radarcarioca/
├── MainActivity.kt                    ← Navigation + Entry point
├── RadarCariocaApp.kt                 ← Application + Hilt + Notification Channel
├── data/
│   ├── local/
│   │   ├── Database.kt                ← Room DB (GeoFeature + RideHistory)
│   │   └── DriverPreferences.kt       ← DataStore (configurações do motorista)
│   └── model/
│       └── Models.kt                  ← Todos os data classes
├── di/
│   └── AppModule.kt                   ← Hilt DI bindings
├── financial/
│   └── FinancialCalculator.kt         ← Motor de cálculo R$/KM, R$/H
├── geo/
│   └── GeoSecurityManager.kt          ← Parser GeoJSON + verificação de risco
├── overlay/
│   └── OverlayManager.kt              ← WindowManager + ComposeView flutuante
├── service/
│   ├── RadarAccessibilityService.kt   ← Leitura automática Uber/99
│   ├── RadarForegroundService.kt      ← Watcher ininterrupto
│   ├── RideProcessor.kt               ← Pipeline geo + financeiro
│   ├── GeocodingService.kt            ← Texto → lat/lng (Mapbox + fallback)
│   └── BootReceiver.kt                ← Reinicia após reboot
└── ui/
    ├── MainViewModel.kt               ← Estado da UI + lógica de negócio
    ├── theme/
    │   └── Theme.kt                   ← Paleta Azul Marinho + Dourado
    └── screens/
        ├── DashboardScreen.kt         ← Painel principal
        ├── OverlayCard.kt             ← Semáforo glassmorphism
        └── OtherScreens.kt            ← Onboarding, Settings, Stats
```

---

## Fluxo de execução (resumo)

```
Uber/99 exibe oferta
        ↓
RadarAccessibilityService captura texto (< 200ms)
        ↓
GeocodingService converte endereço → lat/lng (< 800ms com internet, instantâneo offline)
        ↓
GeoSecurityManager.checkSafety(lat, lng) — verifica GeoJSON local
        ↓
FinancialCalculator.calculate() — R$/KM, R$/H, lucro líquido
        ↓
OverlayManager.showAnalysis() — exibe card sobre o app
        ↓
TOTAL: < 1.500ms  ✓
```

---

## Testes sem dispositivo físico

Para simular ofertas no emulador, use o ViewModel diretamente ou
adicione um botão de debug no DashboardScreen que injeta uma `RideOffer`
fake no `RadarAccessibilityService.rideOfferFlow`.

---

*Radar Carioca — Seu copiloto nas ruas do Rio*
