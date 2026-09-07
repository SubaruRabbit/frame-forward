## 1. Contract

- [x] 1.1 In `contracts`, add the additive `PortfolioWorkWorkflowContext` and nested nullable reference schemas to the owned-work detail response; update the OpenAPI baseline and verify the contract test accepts the documented response.

## 2. Server read projection

- [x] 2.1 In `frame-forward-server/evaluation`, first add failing query tests for latest owned evaluation, owned session, and completed same-session candidate isolation; implement the minimal account-scoped projection and verify the evaluation-module tests pass.
- [x] 2.2 In `frame-forward-server/portfolio`, first extend service/controller tests for typed workflow context, absent associations, deletion and foreign-work isolation; assemble the projection into detail responses and verify the portfolio-module tests pass.

## 3. App boundary and quality

- [x] 3.1 In `frame-forward-app/features/portfolio`, map `workflowContext` from the network DTO into a typed domain model without exposing raw API maps to presentation; verify mapper and PortfolioScreen tests cover present and unavailable contexts.
- [x] 3.2 In `frame-forward-server`, run `mvn spotless:apply` and `QUALITY_BASE_REF=HEAD mvn verify`; verify Java formatting, contract, tests, coverage and static quality pass.
- [x] 3.3 In `frame-forward-app`, run `npm run quality`; verify format checking, zero-warning linting, type checking and Jest suites pass.
