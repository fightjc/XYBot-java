package org.fightjc.xybot.module.genshin.pojo;

public class ConstellationBean {
    String name;
    ConstellationDetailBean c1;
    ConstellationDetailBean c2;
    ConstellationDetailBean c3;
    ConstellationDetailBean c4;
    ConstellationDetailBean c5;
    ConstellationDetailBean c6;

    public ConstellationBean(String name, ConstellationDetailBean c1, ConstellationDetailBean c2, ConstellationDetailBean c3,
                             ConstellationDetailBean c4, ConstellationDetailBean c5, ConstellationDetailBean c6) {
        this.name = name;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
        this.c5 = c5;
        this.c6 = c6;
    }

    public String getName() {
        return name;
    }

    public ConstellationDetailBean getC1() {
        return c1;
    }

    public ConstellationDetailBean getC2() {
        return c2;
    }

    public ConstellationDetailBean getC3() {
        return c3;
    }

    public ConstellationDetailBean getC4() {
        return c4;
    }

    public ConstellationDetailBean getC5() {
        return c5;
    }

    public ConstellationDetailBean getC6() {
        return c6;
    }

    public static class ConstellationDetailBean {
        String name;
        String effect;

        public ConstellationDetailBean(String name, String effect) {
            this.name = name;
            this.effect = effect;
        }

        public String getName() {
            return name;
        }

        public String getEffect() {
            return effect;
        }
    }
}
