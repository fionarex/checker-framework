import org.checkerframework.checker.trust.qual.Trust;

@Trust(level = 2) 
class TrustLevelTwo {
  public TrustLevelTwo() {}
  
  int m1(@Trust(level = 1) TrustLevelTwo this, int val) {
    return val + 1;
  }

  int m2(@Trust(level = 2) TrustLevelTwo this, int val) {
    return val + 2;
  }

  int m3(@Trust(level = 3) TrustLevelTwo this, int val) {
    return val + 3;
  }
}

public class Basic {
  void test() {
    TrustLevelTwo o = new TrustLevelTwo();
    
    int result;

    result = o.m1(10);
    result = o.m2(result);

    // :: error: (trust.level.too.low)
    result = o.m3(result);
  }
}