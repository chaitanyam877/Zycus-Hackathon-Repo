# 🛒 Intelligent Commerce Recommendation Engine

An event-driven backend system that monitors product inventory and demand, automatically detects commerce-related triggers, and generates actionable **pricing** and **reorder recommendations**.

The system is designed around an agentic architecture where inventory changes trigger an asynchronous decision-making workflow. Recommendations are persisted for human review and can be accepted or rejected through REST APIs.

---

## 🚀 Features

- Product and inventory management
- Order simulation through REST APIs
- Event-driven inventory processing
- Asynchronous inventory event listener
- Automatic trigger detection
- Inventory-low detection
- Demand-spike detection
- Category-level demand analysis
- Rule-based pricing recommendations
- Rule-based reorder recommendations
- Recommendation confidence scores
- Human approval/rejection workflow
- H2 database persistence
- DTO-based API responses
- Transaction-safe persistence
- Validation for recommendation decisions
- Clean separation between:
  - Controllers
  - Services
  - Domain entities
  - Events
  - Repositories
  - Recommendation strategies

---

# 🏗️ Architecture

```text
                         REST API
                            │
                            ▼
                    ProductController
                            │
                            ▼
                     ProductService
                            │
                  ┌─────────┴─────────┐
                  │                   │
            Update Product       Publish Event
                  │                   │
                  └─────────┬─────────┘
                            ▼
                  InventoryChangedEvent
                            │
                            ▼
                InventoryEventListener
                       (@Async)
                            │
                            ▼
                  AgenticLoopService
                            │
                 ┌──────────┴──────────┐
                 │                     │
          Trigger Detection      Category Analysis
                 │                     │
                 └──────────┬──────────┘
                            ▼
                CommerceAdvisorService
                            │
                            ▼
                RuleBasedCommerceAdvisor
                            │
                  ┌─────────┴─────────┐
                  │                   │
                  ▼                   ▼
          Pricing Recommendation   Reorder Recommendation
                  │                   │
                  ▼                   ▼
          PricingSuggestion       ReorderSuggestion
                  │                   │
                  └─────────┬─────────┘
                            ▼
                           H2
                            │
                            ▼
                    REST API / Frontend
                            │
                            ▼
                    Human Decision
                  ┌─────────┴─────────┐
                  │                   │
               ACCEPT              REJECT
                  │
                  ▼
             Update Product
