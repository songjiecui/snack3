package features;

import _models.UserModel3;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

/**
 * @author noear 2021/12/10 created
 */
public class AttrTest2 {
    @Test
    public void test() {
        UserModel3 user = new UserModel3();

        user.id = 1;
        user.name = "noear";
        user.note = "test";

        String json = ONode.stringify(user);

        System.out.println(json);

        assert json.contains("noear") == false;
        assert json.contains("test") == false;
    }

    @Test
    public void test1() {
        UserModel3 user = new UserModel3();

        user.id = 1;
        user.name = "noear";
        user.note = "test";

        Options options = new Options();
        options.add(Feature.SerializeNulls);

        String json = ONode.stringify(user, options);

        System.out.println(json);

        assert json.contains("noear") == false;
        assert json.contains("test") == false;
        assert json.contains("nullVal") == false;
    }

    @Test
    public void test2() {
        String json = "{id:1, name:'noear', note:'test'}";


        UserModel3 user = ONode.deserialize(json, UserModel3.class);

        System.out.println(json);

        assert user.id == 1;
        assert user.name == null;
        assert user.note == null;
    }
}
