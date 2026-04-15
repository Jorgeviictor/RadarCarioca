# Radar Carioca — Diagrama de Arquitetura

## Fluxo de Dados Principal

```mermaid
flowchart TD
    subgraph EXTERNO["📱 Apps Externos"]
        UBER[Uber App]
        APP99[99 App]
    end

    subgraph ANDROID["🤖 Android Services"]
        ACC["RadarAccessibilityService\n(captura ofertas)"]
        FGS["RadarForegroundService\n(orquestrador)"]
        BOOT["BootReceiver\n(auto-start)"]
    end

    subgraph DOMAIN["🟢 Domain Layer (Kotlin puro)"]
        direction TB
        UC1["ProcessRideOfferUseCase"]
        UC2["CheckRideSafetyUseCase"]
        UC3["CalculateRideProfitUseCase"]
        GEO_REPO[/"GeoRepository (interface)"/]
        HIST_REPO[/"RideHistoryRepository (interface)"/]
    end

    subgraph DATA["🔵 Data Layer (Room + Mappers)"]
        GEO_IMPL["GeoRepositoryImpl"]
        HIST_IMPL["RideHistoryRepositoryImpl"]
        GEO_MAP["GeoFeatureMapper"]
        RIDE_MAP["RideRecordMapper"]
        GEO_DAO["GeoFeatureDao"]
        HIST_DAO["RideHistoryDao"]
        DB[("RadarDatabase\n(Room)")]
        PREFS["DriverPreferences\n(DataStore)"]
        GEO_ENTITY["GeoFeatureEntity"]
        RIDE_ENTITY["RideRecordEntity"]
    end

    subgraph GEO_SVC["🗺️ Geo Engine"]
        GEO_MGR["GeoSecurityManager\n(haversine, ray-casting)"]
        GEOCODE["GeocodingService\n(endereço → lat/lng)"]
        GEOJSON[("mapa_faccoes_rj.geojson\n(assets)")]
    end

    subgraph PRESENTATION["🎨 Presentation Layer (Compose)"]
        VM["MainViewModel\n(StateFlow)"]
        UISTATE["DashboardUiState"]
        DASH["DashboardScreen"]
        OVERLAY["OverlayCard\n(glassmorphism)"]
        PAYWALL["PaywallScreen"]
        NAV["Navigation Graph"]
    end

    subgraph DI["💉 Hilt DI"]
        DB_MOD["DatabaseModule"]
        REPO_MOD["RepositoryModule\n(@Binds interface → impl)"]
    end

    subgraph BILLING["💳 Billing"]
        BILL_MGR["BillingManager\n(Play Billing)"]
    end

    %% Fluxo principal de corrida
    UBER -->|"accessibility event"| ACC
    APP99 -->|"accessibility event"| ACC
    ACC -->|"RideOffer (SharedFlow)"| FGS
    BOOT -->|"start on boot"| FGS
    FGS -->|"invoke"| UC1

    %% Use Case orquestra
    UC1 -->|"geocode"| GEOCODE
    UC1 -->|"invoke"| UC2
    UC1 -->|"invoke"| UC3
    UC2 -->|"checkSafety()"| GEO_MGR
    GEO_MGR -->|"usa interface"| GEO_REPO
    GEO_MGR -->|"carrega"| GEOJSON

    %% Repositório geo
    GEO_REPO -.->|"implementado por"| GEO_IMPL
    GEO_IMPL -->|"usa"| GEO_MAP
    GEO_IMPL -->|"acessa"| GEO_DAO
    GEO_MAP <-->|"converte"| GEO_ENTITY
    GEO_DAO -->|"persiste"| DB

    %% Repositório histórico
    HIST_REPO -.->|"implementado por"| HIST_IMPL
    HIST_IMPL -->|"usa"| RIDE_MAP
    HIST_IMPL -->|"acessa"| HIST_DAO
    RIDE_MAP <-->|"converte"| RIDE_ENTITY
    HIST_DAO -->|"persiste"| DB

    %% Resultado → Overlay
    UC1 -->|"RideAnalysis"| FGS
    FGS -->|"showAnalysis()"| OVERLAY
    FGS -->|"insert(RideRecord)"| HIST_REPO

    %% ViewModel → UI
    VM -->|"observa"| HIST_REPO
    VM -->|"observa"| PREFS
    VM -->|"observa"| BILL_MGR
    VM -->|"emite"| UISTATE
    UISTATE -->|"collectAsState()"| DASH
    DASH -->|"navega para"| NAV
    NAV -->|"route paywall"| PAYWALL

    %% DI wiring
    DB_MOD -.->|"fornece"| DB
    DB_MOD -.->|"fornece"| GEO_DAO
    DB_MOD -.->|"fornece"| HIST_DAO
    REPO_MOD -.->|"@Binds"| GEO_REPO
    REPO_MOD -.->|"@Binds"| HIST_REPO

    %% Estilos
    classDef domain    fill:#22c55e,color:#fff,stroke:#16a34a
    classDef data      fill:#3b82f6,color:#fff,stroke:#2563eb
    classDef present   fill:#a855f7,color:#fff,stroke:#9333ea
    classDef android   fill:#f59e0b,color:#fff,stroke:#d97706
    classDef di        fill:#6b7280,color:#fff,stroke:#4b5563
    classDef ext       fill:#ef4444,color:#fff,stroke:#dc2626

    class UC1,UC2,UC3,GEO_REPO,HIST_REPO domain
    class GEO_IMPL,HIST_IMPL,GEO_MAP,RIDE_MAP,GEO_DAO,HIST_DAO,DB,PREFS,GEO_ENTITY,RIDE_ENTITY data
    class VM,UISTATE,DASH,OVERLAY,PAYWALL,NAV present
    class ACC,FGS,BOOT,GEO_MGR,GEOCODE android
    class DB_MOD,REPO_MOD di
    class UBER,APP99 ext
```

---

## Estrutura de Pacotes

```
app/src/main/java/com/radarcarioca/
│
├── domain/                          ← 🟢 KOTLIN PURO (zero imports Android)
│   ├── repository/
│   │   ├── GeoRepository.kt         ← interface de contrato geo
│   │   └── RideHistoryRepository.kt ← interface de contrato histórico
│   └── usecase/
│       ├── CheckRideSafetyUseCase.kt
│       ├── CalculateRideProfitUseCase.kt
│       └── ProcessRideOfferUseCase.kt
│
├── data/                            ← 🔵 CAMADA DE DADOS (conhece Room)
│   ├── model/
│   │   └── Models.kt                ← entidades de domínio PURAS (sem @Entity)
│   ├── local/
│   │   ├── entity/
│   │   │   ├── GeoFeatureEntity.kt  ← @Entity Room (isolado do domain)
│   │   │   └── RideRecordEntity.kt  ← @Entity Room (isolado do domain)
│   │   ├── Database.kt              ← DAOs + RadarDatabase
│   │   └── DriverPreferences.kt     ← DataStore
│   ├── mapper/
│   │   ├── GeoFeatureMapper.kt      ← Entity ↔ Domain
│   │   └── RideRecordMapper.kt      ← Entity ↔ Domain
│   └── repository/
│       ├── GeoRepositoryImpl.kt     ← implementa GeoRepository
│       └── RideHistoryRepositoryImpl.kt
│
├── geo/
│   └── GeoSecurityManager.kt        ← motor geoespacial (usa GeoRepository)
│
├── financial/
│   └── FinancialCalculator.kt       ← cálculo financeiro puro
│
├── service/                         ← 🤖 ANDROID SERVICES
│   ├── RadarAccessibilityService.kt
│   ├── RadarForegroundService.kt
│   ├── RideProcessor.kt
│   ├── GeocodingService.kt
│   └── BootReceiver.kt
│
├── ui/                              ← 🎨 PRESENTATION (Compose)
│   ├── MainViewModel.kt             ← usa RideHistoryRepository (interface!)
│   └── screens/
│       ├── DashboardScreen.kt
│       ├── OverlayCard.kt
│       ├── PaywallScreen.kt
│       └── OtherScreens.kt
│
├── di/                              ← 💉 HILT
│   ├── DatabaseModule.kt            ← Room + DAOs
│   └── RepositoryModule.kt          ← @Binds interface → implementação
│
└── billing/
    └── BillingManager.kt
```

---

## Regras de Dependência (Clean Architecture)

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION                         │
│   (ViewModel, Compose Screens, Navigation)              │
│         depende de → Domain (Use Cases)                 │
└─────────────────────┬───────────────────────────────────┘
                      │ depende de ↓
┌─────────────────────▼───────────────────────────────────┐
│                      DOMAIN                             │
│   (Entities, Repository Interfaces, Use Cases)          │
│         NÃO depende de nada externo                     │
└─────────────────────┬───────────────────────────────────┘
                      │ implementado por ↓
┌─────────────────────▼───────────────────────────────────┐
│                       DATA                              │
│   (RepositoryImpl, DAOs, Room Entities, Mappers)        │
│         depende de → Domain (interfaces)                │
│         conhece → Room, DataStore                       │
└─────────────────────────────────────────────────────────┘
```

---

## Ciclo de Vida de uma Corrida

```
Uber/99 exibe oferta
        │
        ▼
RadarAccessibilityService   ← lê texto via Accessibility API
        │  RideOffer
        ▼
RadarForegroundService      ← recebe via SharedFlow
        │
        ▼
ProcessRideOfferUseCase
  ├─ GeocodingService       ← endereço → lat/lng
  ├─ CheckRideSafetyUseCase
  │    └─ GeoSecurityManager
  │         └─ GeoRepository  ← lê features do Room
  └─ CalculateRideProfitUseCase
       └─ FinancialCalculator  ← lucro líquido, R$/KM, margem
        │
        ▼ RideAnalysis
 ┌──────┴──────────┐
 ▼                 ▼
OverlayCard    RideHistoryRepository  ← persiste no Room
(GREEN/YELLOW/
 RED/PURPLE)
```

---

## Legenda de Cores

| Cor | Camada |
|-----|--------|
| 🟢 Verde | Domain (Kotlin puro) |
| 🔵 Azul | Data (Room, mappers) |
| 🟣 Roxo | Presentation (Compose) |
| 🟠 Laranja | Android Services |
| ⚫ Cinza | DI (Hilt) |
| 🔴 Vermelho | Externos (Uber/99) |
