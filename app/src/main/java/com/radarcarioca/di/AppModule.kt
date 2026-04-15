package com.radarcarioca.di

// Este arquivo não registra nenhuma dependência e pode ser removido do projeto.
//
// Histórico: foi o módulo Hilt original, substituído pelos módulos especializados:
//   - DatabaseModule.kt  → Room Database e todos os DAOs
//   - RepositoryModule.kt → Bindings Interface ↔ Implementação (repositórios)
//   - ServiceModule.kt   → Bindings Interface ↔ Implementação (serviços)
//   - AlertModule.kt     → Firebase, dispatcher e data sources de alertas
//   - Qualifiers.kt      → Qualifiers type-safe (@IoDispatcher)
//
// Para remover: delete este arquivo. O grafo do Hilt não será afetado.
