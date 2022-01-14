# mandarax
Mandarax is a rule compiler combining OOP and logic programming concepts, creating systems with built-in provenance (deprecated).

This project is not longer maintained ! 

To get an idea about how this works, try the userv sample application. The UI can be started with `org.mandarax.examples.userv.app.Main`. This is based on business rules
defining relations (defined in `src/main/java/org/mandarax/examples/userv/rules`) which are compiled into Java classes (`src/main/java/org/mandarax/examples/userv/rules/generated`) by the mandarax compiler `org.mandarax.compiler.impl.DefaultCompiler`. 

The generated classes provide a straight-forward API to query the relations (with their semantics defined by external data sources, derivation rules, or some other means). For instance

`org.mandarax.examples.userv.rules.generated.BasePremiumRelInstances` defines `ResultSet<BasePremiumRel> getPremium (Car car)`. The result set is a DB (JDBC) - like 
iterator, but also contains an API `List<DerivationLogEntry> getDerivationLog()` to provide provenance, i.e. the rules and data used to compute a particular result.  
