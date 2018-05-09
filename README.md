# clj-bf-graal

Clojure conversion of https://github.com/cesquivias/bf-graal

Running:

```sh
export JAVA_HOME=graalvm-1.0.0-rc1

graalvm-1.0.0-rc1/bin/java -cp $PWD/../clj-graal-bf-lang/target/project.jar:$PWD/target/project.jar -XX:-UseJVMCIClassLoader language.bf ./examples/mandelbrot.bf
```