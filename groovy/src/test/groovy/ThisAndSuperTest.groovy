package groovy

class ThisAndSuperTest extends GroovyTestCase{
	void testOverwrittenSuperMethod(){
		def helper = new TestForSuperHelper2()
		assert helper.foo() == 2
		assert helper.callFooInSuper() == 1
	}
		
	void testClosureUsingSuperAndThis(){
		def helper = new TestForSuperHelper2()
		assert helper.aClosureUsingThis() == 2
		assert helper.aClosureUsingSuper() == 1
  		// accessing private method should not be changed
  		// by a public method of the same name and signature!
		assertEquals "bar", helper.closureUsingPrivateMethod() 
		assert helper.bar() == "no bar"
		
		assert helper.aField == "I am a field"
		helper.closureFieldAccessUsingImplicitThis(1)
        assert helper.aField == 1
 		helper.closureFieldAccessUsingExplicitThis(2)
 		assert helper.aField == 2
	}
	
	void testClosureDelegateAndThis(){
		def map = [:]
		def helper = new TestForSuperHelper2()
		
		helper.aField = "I am a field"
		helper.closureFieldAccessUsingExplicitThis.delegate = map
		helper.closureFieldAccessUsingExplicitThis(3)
 		assert helper.aField == 3
 		assert map.aField == null

		helper.aField = "I am a field"
		helper.closureFieldAccessUsingImplicitThis.delegate = map
		helper.closureFieldAccessUsingImplicitThis(4)
 		assert helper.aField == 4
 		assert map.aField == null
 		
 		def closure = {this.foo = 1}
		shouldFail {
		  closure()
		}
		closure.delegate = map
		shouldFail {
		  closure()
		}
		assert map.foo == null
		
		closure = {foo = 1}
		shouldFail {
		  closure()
		}
		closure.delegate = map
		closure()
		assert map.foo == 1
	}
	
	void testConstructorChain() {
		def helper = new TestForSuperHelper4()
		assert helper.x == 1
		helper = new TestForSuperHelper4("foo")
		assert helper.x == "Object"
	}
	
	void testChainingForAsType() {
		def helper = new TestForSuperHelper1()
		def ret = helper as Object[]
		assert ret instanceof Object[]
		assert ret[0] == helper
		
		shouldFail(ClassCastException) {
		  helper as Integer
		}
	}

    void testSuperEach () {
        def x = new TestForSuperEach()
        x.each {
            x.res << "I am it: ${it.class.name}"
        }

        assertEquals 3, x.res.size()
        assertEquals "start each in subclass", x.res [0]
        assertEquals "I am it: groovy.TestForSuperEach", x.res [1]
        assertEquals "end of each in subclass", x.res [2]
    }

    void testDgm () {
      assertEquals A.empty(), '123'
    }
}

class A {
   static {
       A.metaClass.static.empty << { -> '123' }
   }
}


class TestForSuperEach {
  def res = []
    
  def each(Closure c) {
      res << "start each in subclass"
      super.each(c)
      res << "end of each in subclass"
  }
}

class TestForSuperHelper1 {
  def foo(){1}
  private bar(){"bar"}
  def closureUsingPrivateMethod() {bar()}
  def asType(Class c) {
    if (c==Object[]) return [this] as Object[]
    return super.asType(c)
  }
}

class TestForSuperHelper2 extends TestForSuperHelper1{
  def foo(){2}
  def callFooInSuper(){super.foo()}
  def aClosureUsingSuper = {super.foo()}
  def aClosureUsingThis = {this.foo()}
  def bar(){"no bar"}
  public aField = "I am a field"
  def closureFieldAccessUsingImplicitThis = {x-> aField=x}
  def closureFieldAccessUsingExplicitThis = {x-> this.aField=x}
}

class TestForSuperHelper3 {
  def x
  TestForSuperHelper3(int i) {
    this("1")
    x=1
  }
  TestForSuperHelper3(Object j) {
    x = "Object"
  }
}

class TestForSuperHelper4 extends TestForSuperHelper3{
  TestForSuperHelper4() {
    super(1)
  }
  TestForSuperHelper4(Object j) {
    super(j)
  }
}
