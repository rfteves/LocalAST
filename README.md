# LocalAST
This is a naive and simple example of Groovy AST Transformation that would add console messages at the start and end of a method invocation. The following Product class would actually starting and ending messages.

```
import localast.WithLogging

class Product {
    String name
    String description
    Double cost
    Double msrp = -1.0

    @WithLogging(properties = ['msrp'])
    def computeMsrp() {
        println 'computeMsrp'
        msrp = cost * 1.25
        msrp
    }

    @WithLogging(properties = ['msrp'])
    void computeMsrpVoid() {
        println 'computeMsrpVoid'
        msrp = cost * 1.25
    }

    static constraints = {
    }
}
```

computeMsrp(), the following will be printed:

Starting computeMsrp -1.0 
computeMsrp

Ending computeMsrp 1.25

computeMsrpVoid(), the following will be printed:

Starting computeMsrpVoid 1.25 
computeMsrpVoid

Ending computeMsrpVoid 1.25
