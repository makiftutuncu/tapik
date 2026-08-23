# Tapik Rewrite TODO

## WebMVC handler and adapter separation

- [ ] Replace the generated WebMVC interface's public Spring mapping bridge with a generated adapter and automatic
  configuration.

  Generate a typed handler interface for each API. It should contain the API value, response types, and one abstract
  method per endpoint. User implementations should deal only with decoded Kotlin values and typed responses; raw wire
  values, Spring mapping annotations, content negotiation, encoding, and `ResponseEntity` should not appear in this
  interface.

  Generate a separate Spring WebMVC adapter for each handler interface. The adapter should own the mapping methods and
  delegate decoded requests to the typed handler. Its mapping methods are framework implementation details rather than
  part of the handler contract, so they do not need endpoint names with an `Http` suffix in the public handler API.

  Register generated adapters automatically. A user should only need to provide a Spring bean implementing the typed
  handler interface; they should not have to declare an adapter bean for every API. Registration should remain explicit
  and deterministic at generation/build time rather than scanning arbitrary packages or reflecting over user classes.
  One handler bean must remain able to implement multiple generated API interfaces.

  Before implementation, decide and specify:

  - how generated adapters and their auto-configuration are published and discovered;
  - whether the initial integration targets Spring Boot only or also provides a plain Spring registration mechanism;
  - how multiple candidate handler beans, qualifiers, conditions, and missing handlers behave;
  - how API instances are supplied to adapters;
  - adapter visibility, naming, proxy compatibility, and generated artifact identity;
  - integration tests covering zero user-written adapter configuration and multi-API handler composition.
