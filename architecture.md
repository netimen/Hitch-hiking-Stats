# Features
1. UDF
1. SolID
1. Platform-specific code only takes orders (no logic)
1. Business-processes are immune to view lifecycle (screen orientation changes, view being destroyed to free memory etc).
1. Still, views don't leak
1. Model-View communications follow push paradigm
1. No UI blocking operations
1. Easy to implement: no over-engineering, weird constructs
1. Extensive usage of Kotlin features for elegant and readable code

# Benefits
1. UDF ⇨ close modelling of real user-app interaction ⇨ easy to understand
1. UDF, SolID ⇨ easy to add/change things
1. more SolID than retaining Presenter: Presenter is dealing with 2 life-cycles actually

# Players
1. Services — databases, network API etc
1. Business Logic — knows how to do things.
1. Usecase — necessary BL calls
1. Interactor — Usecase + caching + threading
1. Processor — logical screen lifecycle

Input output widgets
processIntent
Big lists 
Система раскладывается на большое количество stateless objects
Dependencies graph is uni-directional? 
