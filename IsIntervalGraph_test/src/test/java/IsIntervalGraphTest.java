import ru.leti.wise.task.graph.util.FileLoader;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IsIntervalGraphTest {
    @Test
    public void intervalGraphCheckTest() throws FileNotFoundException {
        IsIntervalGraph check = new IsIntervalGraph();
        var test_1 = FileLoader.loadGraphFromJson("src/test/resources/Test_1.json");
        var test_2 = FileLoader.loadGraphFromJson("src/test/resources/Test_2.json");
        var test_3 = FileLoader.loadGraphFromJson("src/test/resources/Test_3.json");
        var test_4 = FileLoader.loadGraphFromJson("src/test/resources/Test_4.json");
        var test_5 = FileLoader.loadGraphFromJson("src/test/resources/Test_5.json");
        var test_6 = FileLoader.loadGraphFromJson("src/test/resources/Test_6.json");
        var test_7 = FileLoader.loadGraphFromJson("src/test/resources/Test_7.json");
        var test_8 = FileLoader.loadGraphFromJson("src/test/resources/Test_8.json");
        var test_9 = FileLoader.loadGraphFromJson("src/test/resources/Test_9.json");
        var test_10 = FileLoader.loadGraphFromJson("src/test/resources/Test_10.json");
        System.out.println("==============Test 1==============");
        assertThat(check.run(test_1)).isEqualTo(1);
        System.out.println("==============Test 2==============");
        assertThat(check.run(test_2)).isEqualTo(0);
        System.out.println("==============Test 3==============");
        assertThat(check.run(test_3)).isEqualTo(0);
        System.out.println("==============Test 4==============");
        assertThat(check.run(test_4)).isEqualTo(0);
        System.out.println("==============Test 5==============");
        assertThat(check.run(test_5)).isEqualTo(0);
        System.out.println("==============Test 6==============");
        assertThat(check.run(test_6)).isEqualTo(1);
        System.out.println("==============Test 7==============");
        assertThat(check.run(test_7)).isEqualTo(1);
        System.out.println("==============Test 8==============");
        assertThat(check.run(test_8)).isEqualTo(1);
        System.out.println("==============Test 9==============");
        assertThat(check.run(test_9)).isEqualTo(1);
        System.out.println("==============Test 10==============");
        assertThat(check.run(test_10)).isEqualTo(1);
    }
}