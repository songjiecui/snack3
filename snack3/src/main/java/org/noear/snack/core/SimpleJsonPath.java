package org.noear.snack.core;

import org.noear.snack.ONode;

/**
 * Simple json path
 *
 * 支持：
 * .name
 * [index] //负数表示倒取
 * [index,index] //负数表示倒取
 * [start:end] //负数表示倒取
 * */
public class SimpleJsonPath {
    public static ONode get(String[] ss, int index, ONode source) {
        ONode tmp = source;
        for (int i = index; i < ss.length; i++) {
            String s = ss[i];

            if("$".equals(s)){
                continue; //当前节点
            }

            if (s.endsWith("]")) {
                String idx_s = s.substring(0, s.length() - 1);

                if ("*".equals(idx_s)) {
                    //[*]
                    ONode tmp2 = new ONode().asArray();
                    for (ONode n1 : tmp.ary()) {
                        ONode n2 = get(ss, i + 1, n1);
                        if (n2.isNull() == false) {
                            tmp2.add(n2);
                        }
                    }
                    return tmp2;
                } else if (idx_s.indexOf(",") > 0) {
                    //[1,4,6]
                    ONode tmp2 = new ONode().asArray();
                    String[] iAry = idx_s.split(",");

                    for (String i1 : iAry) {
                        ONode n1 = tmp.get(Integer.parseInt(i1));
                        ONode n2 = get(ss, i + 1, n1);
                        if (n2.isNull() == false) {
                            tmp2.add(n2);
                        }
                    }
                    return tmp2;

                } else if (idx_s.indexOf(":") > 0) {
                    //[2:4]
                    ONode tmp2 = new ONode().asArray();
                    String[] iAry = idx_s.split(":");
                    int count = tmp.count();
                    int start = 0;
                    if (iAry[0].length() > 0) {
                        start = Integer.parseInt(iAry[0]);
                        if (start < 0) {//如果是倒数？
                            start = count + start;
                        }
                    }
                    int end = count;
                    if (iAry[1].length() > 0) {
                        end = Integer.parseInt(iAry[1]);
                        if (end < 0) { //如果是倒数？
                            end = count + end;
                        }
                    }

                    for (int i1 = start; i1 < end; i1++) {
                        ONode n1 = tmp.get(i1);
                        ONode n2 = get(ss, i + 1, n1);
                        if (n2.isNull() == false) {
                            tmp2.add(n2);
                        }
                    }
                    return tmp2;

                } else {
                    // [2]
                    // [-2]
                    //
                    int idx = Integer.parseInt(idx_s);
                    if (idx < 0) {
                        tmp = tmp.get(tmp.count() + idx);//倒数位
                    } else {
                        tmp = tmp.get(idx);//正数位
                    }
                }
            } else {
                //name
                tmp = tmp.get(s);
            }
        }

        return tmp;
    }
}
