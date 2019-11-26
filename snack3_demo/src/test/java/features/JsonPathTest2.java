package features;

import org.junit.Test;
import org.noear.snack.ONode;

import java.util.List;
import java.util.Map;

public class JsonPathTest2 {
    @Test
    public void test1(){
        final String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}},\"expensive\":10}";

        ONode n = ONode.load(json);

        String t1 = n.select("$.store.book [0].title").getString();
        System.out.println(t1);
        assert "Sayings of the Century".equals(t1);

        String t2 = n.select("$['store']['book'][0]['title']").getString();
        System.out.println(t2);
        assert "Sayings of the Century".equals(t1);

        ONode t3 = n.select("$.store.book[*].author");
        System.out.println(t3);

        ONode t4 = n.select("$..author");
        System.out.println(t4);
        assert t3.toJson().equals(t4.toJson());

        ONode t5 = n.select("$.store.*");
        System.out.println(t5);
        assert t5.count() == 2;

        ONode t6 = n.select("$.store..price");
        System.out.println(t6);
        assert t6.isArray();

        Map<String,Object> t7 = n.select("$..book[2]").toObject(Map.class);
        System.out.println(t7);

        ONode t8 =  n.select("$..book[-2]");
        System.out.println(t8);
        assert "Herman Melville".equals(t8.get("author").getString());

        ONode t9 = n.select("$..book[0,1]");
        System.out.println(t9);
        assert "reference".equals(t9.get(0).get("category").getString());

        ONode ta = n.select("$..book[:2]");
        System.out.println(ta);
        assert ta.count()==2;

        ONode tb = n.select("$..book[1:2]");
        System.out.println(tb);
        assert tb.count()==1;

        ONode tc = n.select("$..book[-2:]");
        System.out.println(tc);
        assert tc.count()==2;

        ONode td = n.select("$..book[2:]");
        System.out.println(td);
        assert tc.count()==2;

        ONode te = n.select("$..book[?(@.isbn)]");
        System.out.println(te);
        assert tc.count()==2;

        ONode tf = n.select("$.store.book[?(@.price < 10)]");
        System.out.println(tf);
        assert tc.count()==2;

        ONode tg = n.select("$..book[?(@.author =~ /.*REES/i)]");
        System.out.println(tg);
        assert tg.count()==1;

        ONode th = n.select("$..*");
        System.out.println(th);
        assert th.count()==28;

        ONode ti = n.select("$..book[?(@.price <= $['expensive'])]");
        System.out.println(ti);
        assert ti.count()==2;
    }

    @Test
    public void test2(){
        String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99,\"isbn\":\"0-553-21311-3\"}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";
        ONode n = ONode.load(json);

        String t1 = n.select("$.store.book[0].author").getString();
        System.out.println(t1);
        assert "Nigel Rees".equals(t1);

        List<String> t2 = n.select("$.store.book[*].author").toObject(List.class);
        System.out.println(t2);
        assert t2.size() == 2;

        List<Map> t3 = n.select("$.store.book[?(@.category == 'reference')]").toObject(List.class);
        System.out.println(t3);
        assert t3.size()==1;

        ONode t4 = n.select("$.store.book[?(@.price > 10)]");
        System.out.println(t4);
        assert t4.isArray() && t4.count()==1;

        ONode t5 = n.select("$.store.book[?(@.isbn)]");
        System.out.println(t5);
        assert t5.isArray() && t5.count()==1;

        List<Double> t6 = n.select("$..price").toObject(List.class);
        System.out.println(t6);

        ONode t7 = n.select("$.store.book[*]");
        System.out.println(t7);
        assert t7.isArray() && t7.count()==2;
    }

    @Test
    public void test3(){
        String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99,\"isbn\":\"0-553-21311-3\"}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";
        ONode n = ONode.load(json);

        List<Map> t3 = n.select("$.store.book[?(@.category == 'reference')]").toObject(List.class);
        System.out.println(t3);
        assert t3.size()==1;

        ONode t4 = n.select("$.store.book[?(@.price > 10)]");
        System.out.println(t4);
        assert t4.isArray() && t4.count()==1;

        ONode t5 = n.select("$.store.book[?(@.isbn)]");
        System.out.println(t5);
        assert t5.isArray() && t5.count()==1;
    }
}
