import com.test.instance.InstanceApplication;
import com.test.instance.test.Car;
import com.test.instance.test.Driver;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * 在单元测试的时候报：java.lang.IllegalStateException: Unable to find a @SpringBootConfiguration
 * 1)单元测试的测试类一定要和启动类在同一个根目录下
 * 2)@SpringBootTest(classes = InstanceApplication.class)指定主启动类
 *
 * @author 费世程
 * @date 2020/7/29 20:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InstanceApplication.class)
public class Test {

  @Autowired
  private Car car;
  @Autowired
  private Driver driver;

  @org.junit.Test
  public void method() {
    /**
     * 在TestConfiguration配置类中
     * 当使用【@Component】时 -> 返回false
     * 当使用【@Configuration】时 -> 返回true
     */
    System.err.println(car == driver.getCar());
  }

}
