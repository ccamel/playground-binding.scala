playground-binding.scala
========================
[![MIT](https://img.shields.io/badge/licence-MIT-lightgrey.svg?style=flat)](https://tldrlegal.com/license/mit-license) [![build-status](https://travis-ci.org/ccamel/playground-binding.scala.svg?branch=master)](https://travis-ci.org/ccamel/playground-binding.scala)

> My playground I use for playing with fancy technologies. This one's for [scala], [scalajs] and [binding.scala].

## Building and Running

The build can be launched with:

```bash
sbt ~fastOptJS
```

Then, open `./index.html` file in your browser.

## Demos

### Calc

Calc is a very simple and basic calculator. You can play with it here: [scalafiddle-calc].

[![calc-overview](./doc/assets/demo-calc.png)]( https://scalafiddle.io/sf/hbwbCOe/0)

#### Implementation details

The model is an immutable case class which holds the state of the calculator. The behaviour is implemented by this model 
as a [partial function](https://www.scala-lang.org/api/current/scala/PartialFunction.html) which accepts tokens (digit, operators).    

The gui maintains the whole model in a single [bindable variable](https://static.javadoc.io/com.thoughtworks.binding/unidoc_2.11/11.0.0-M1/index.html#com.thoughtworks.binding.Binding$$Var),
and every graphical element of the calculator needing to be updated upon model change (reactive dom) is bound to that variable.  

## Technologies

[![scala-logo][scala-logo]][scala]

[![scalajs-logo][scalajs-logo]][scalajs]

[![binding.scala-logo][binding.scala-logo]][binding.scala]

[![materializecss-logo][materializecss-logo]][materializecss]

## License

[MIT] © [Chris Camel]

[scala]: https://www.scala-lang.org/
[scala-logo]: doc/assets/logo-scala.png

[scalajs]: https://www.scala-js.org/
[scalajs-logo]: doc/assets/logo-scalajs.png
[binding.scala]: https://github.com/ThoughtWorksInc/Binding.scala
[binding.scala-logo]: doc/assets/logo-binding.scala.png
[materializecss]: http://materializecss.com/
[materializecss-logo]: doc/assets/logo-materializecss.png

[scalafiddle-calc]: https://scalafiddle.io/sf/hbwbCOe/0

[Chris Camel]: https://github.com/ccamel
[MIT]: https://tldrlegal.com/license/mit-license
