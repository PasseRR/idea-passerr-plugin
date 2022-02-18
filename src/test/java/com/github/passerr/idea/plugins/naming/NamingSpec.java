package com.github.passerr.idea.plugins.naming;

/**
 * @author xiehai
 * @date 2022/02/14 17:57
 */
public class NamingSpec {
    public static void main(String[] args) {
        String s = "_HELLO_WORLD";

        System.out.println(NamingStyle.LOWER_KEBAB.toggle(NamingStyle.UPPER_SNAKE.pascal(s)));
    }
}
