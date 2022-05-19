package backend;

import org.junit.jupiter.api.Test;

public class ClassBlackBlackBoxTest extends BaseBlackBoxTest {
    @Test
    public void testTestToStringMethod() {
        String src = """
                class DevonshireCream {
                  serveOn() {
                    return "Scones";
                  }
                }
                                
                print DevonshireCream; // Prints "DevonshireCream".
                """;
        runAndAssert(src, "DevonshireCream");
    }

    @Test
    public void testInstance() {
        String src = """
                class Bagel {}
                var bagel = Bagel();
                print bagel; // Prints "Bagel instance".
                """;
        runAndAssert(src, "Bagel instance");
    }

    @Test
    public void testMethods() {
        String src = """
                class Bacon {
                    eat() {
                        print "Crunch crunch crunch!";
                    }
                }
                Bacon().eat();
                """;
        runAndAssert(src, "Crunch crunch crunch!");
    }

    @Test
    public void testPropertyAccess() {
        String src = """
                class Bacon {
                }
                var bacon = Bacon();
                bacon.message = "good";
                print bacon.message;
                """;
        runAndAssert(src, "good");
    }

    @Test
    public void testFunctionProperty() {
        String src = """
                class Egotist {
                  speak() {
                    print this;
                  }
                }

                var method = Egotist().speak;
                method();
                """;
        runAndAssert(src, "Egotist instance");
    }

    @Test
    public void testFirstClassFunction() {
        String src = """
                class Person {
                  sayName() {
                    print this.name;
                  }
                }
                            
                var jane = Person();
                jane.name = "Jane";
                            
                var method = jane.sayName;
                method();
                    """;
        runAndAssert(src, "Jane");
    }

    @Test
    public void testThis() {
        String src = """
                class Cake {
                  taste() {
                    var adjective = "delicious";
                    print "The " + this.flavor + " cake is " + adjective + "!";
                  }
                }

                var cake = Cake();
                cake.flavor = "German chocolate";
                cake.taste(); // Prints "The German chocolate cake is delicious!".
                """;
        runAndAssert(src, "The German chocolate cake is delicious!");
    }

    @Test
    public void testThis2() {
        String src = """
                class Thing {
                  getCallback() {
                    fun localFunction() {
                      print this;
                    }

                    return localFunction;
                  }
                }

                var callback = Thing().getCallback();
                callback();
                """;
        runAndAssert(src, "Thing instance");
    }

    @Test
    public void testDanglingThis() {
        String src = """
                print this;
                """;
        runAndAssert(src, "[line 1, column 11] error at 'this': Can't use 'this' outside of a class.\n");
    }

    @Test
    public void testDanglingThis2() {
        String src = """
                fun notAMethod() {
                  print this;
                }
                notAMethod();
                """;
        runAndAssert(src, "[line 2, column 32] error at 'this': Can't use 'this' outside of a class.\n");
    }

    @Test
    public void testInit() {
        String src = """
                class Foo {
                  init() {
                    print this;
                  }
                }

                var foo = Foo();
                print foo.init();
                """;
        runAndAssert(src, "Foo instanceFoo instanceFoo instance");
    }

    @Test
    public void testReturnFromInit() {
        String src = """
                class Foo {
                  init() {
                    return "something else";
                  }
                }
                """;
        runAndAssert(src, "[line 3, column 34] error at 'return': Can't return a value from an initializer.\n");
    }

    @Test
    public void testInheritFromNonClass() {
        String src = """
                var NotAClass = "I am totally not a class";
                class Subclass < NotAClass {} // ?!
                """;
        runAndAssert(src, "Superclass must be a class.\n" +
                "[line 2, column 71 ]\n");
    }

    @Test
    public void testCallMethodFromSuperClass() {
        String src = """
                class Doughnut {
                  cook() {
                    print "Fry until golden brown.";
                  }
                }
                            
                class BostonCream < Doughnut {}

                BostonCream().cook();
                """;
        runAndAssert(src, "Fry until golden brown.");
    }

    @Test
    public void testSuperKeyword() {
        String src = """
                class Doughnut {
                  cook() {
                    print "Fry until golden brown.";
                  }
                }
                
                class BostonCream < Doughnut {
                  cook() {
                    super.cook();
                    print "Pipe full of custard and coat with chocolate.";
                  }
                }
                
                BostonCream().cook();
                """;
        runAndAssert(src, "Fry until golden brown.Pipe full of custard and coat with chocolate.");
    }

    @Test
    public void testSuper2() {
        String src = """
                class A {
                  method() {
                    print "A method";
                  }
                }
                
                class B < A {
                  method() {
                    print "B method";
                  }
                
                  test() {
                    super.method();
                  }
                }
                
                class C < B {}
                
                C().test();
                """;
        runAndAssert(src, "A method");
    }

    @Test
    public void testInvalidUseOfSuper() {
        String src = """
                class Eclair {
                  cook() {
                    super.cook();
                    print "Pipe full of crème pâtissière.";
                  }
                }
                """;

        String expect = "[line 3, column 36] error at 'super': Can't use 'super' in a class with no superclass.\n";
        runAndAssert(src, expect);
    }
}
