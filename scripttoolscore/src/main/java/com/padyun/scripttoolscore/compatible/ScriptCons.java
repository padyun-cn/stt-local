package com.padyun.scripttoolscore.compatible;

import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 1/21/19
 */
public class ScriptCons {



    /**
     * base class for java-test
     */
    public static class JRelation {
        static final int FLAG_NOT_NEG = -0x10;
        static final int FLAG_NONE = 0x00;
        public static final int FLAG_AND = 0x01;
        static final int FLAG_OR = 0x02;
        static final int FLAG_NOT = 0x10;
        public static final int FLAG_AND_NOT = FLAG_AND | FLAG_NOT;
        static final int FLAG_OR_NOT = FLAG_OR | FLAG_NOT;

        public static String getRelationDesc(int relation) {
            return Useless.bit_contains(FLAG_AND, relation) ? "并且" : Useless.bit_contains(FLAG_OR, relation) ? "或者" : "";
        }

        public static boolean hasAnd(int relation){
            return (relation & FLAG_AND) == FLAG_AND;
        }

        public static boolean hasOr(int relation){
            return (relation & FLAG_OR) == FLAG_OR;
        }

        public static boolean hasNot(int relation){
            return (relation & FLAG_NOT) == FLAG_NOT;
        }

        public static int and(int relation){
            return (relation & FLAG_NOT) | FLAG_AND;
        }

        public static int or(int relation){
            return (relation & FLAG_NOT) | FLAG_OR;
        }

        public static int not(int relation) {
            return relation | FLAG_NOT;
        }

        public static int yes(int relation) {
            return (relation & FLAG_AND) == FLAG_AND ? FLAG_AND : FLAG_OR;
        }

        static String getRelationBoolean(int relation) {
            return getRelationBoolean(relation, null, null);
        }

        public static String getRelationBoolean(int relation, String defaultFalse, String defualtTrue) {
            if (defaultFalse == null) defaultFalse = "为假";
            if (defualtTrue == null) defualtTrue = "为真";
            return Useless.bit_contains(FLAG_NOT, relation) ? defaultFalse : defualtTrue;
        }

        public static int getAppendedRelationWithFlag(int relation, int flag) {
            int result = relation;
            switch (flag) {
                case FLAG_AND:
                    result = flagChangeAndOrNot(FLAG_OR, relation, flag);
                    break;
                case FLAG_OR:
                    result = flagChangeAndOrNot(FLAG_AND, relation, flag);
                    break;
                case FLAG_NOT:
                    result = flagChangeAndOrNot(FLAG_NOT, relation, flag);
                    break;
                case FLAG_NOT_NEG:
                    result = flagChangeAndOrNot(FLAG_NOT, relation, FLAG_NONE);
                    break;
                default:
            }
            return result;
        }

        private static int flagChangeAndOrNot(int remove, int container, int flag) {
            if (Useless.bit_contains(remove, container)) {
                container &= ~remove;
            }
            return container | flag;
        }

        public static void main(String[] args) {
//            System.out.println("test: FLAG_AND_NOT  (存在为假，否则为真)     -> " + getRelationBoolean(FLAG_AND_NOT));
//            System.out.println("test: FLAG_OR_NOT   (存在为假，否则为真)     -> " + getRelationBoolean(FLAG_OR_NOT));
//            System.out.println("test: FLAG_AND      (存在为假，否则为真)     -> " + getRelationBoolean(FLAG_AND));
//            System.out.println("test: FLAG_OR       (存在为假，否则为真)     -> " + getRelationBoolean(FLAG_OR));
//            System.out.println();
//            System.out.println("test: FLAG_AND_NOT  (存在为与，否则未知)     -> " + getRelationDesc(FLAG_AND_NOT));
//            System.out.println("test: FLAG_AND      (存在为与，否则未知)     -> " + getRelationDesc(FLAG_AND));
//            System.out.println("test: FLAG_OR_NOT   (存在为或，否则未知)     -> " + getRelationDesc(FLAG_OR_NOT));
//            System.out.println("test: FLAG_OR       (存在为或，否则未知)     -> " + getRelationDesc(FLAG_OR));
//
//            System.out.println();
//            int relation = FLAG_NONE;
//            System.out.println("test: FLAG_NONE(0) and FLAG_AND(1)   -> " + (relation = getAppendedRelationWithFlag(relation, FLAG_AND)));
//            System.out.println("test: FLAG_NONE(0) and FLAG_AND(1)   -> " + (relation = getAppendedRelationWithFlag(relation, FLAG_NOT)));
//            System.out.println("test: FLAG_NONE(0) and FLAG_AND(1)   -> " + (relation = getAppendedRelationWithFlag(relation, FLAG_NOT)));
//            System.out.println("test: FLAG_NONE(0) and FLAG_AND(1)   -> " + (relation = getAppendedRelationWithFlag(relation, FLAG_NOT_NEG)));
//            System.out.println("test: FLAG_NONE(0) and FLAG_AND(1)   -> " + (relation = getAppendedRelationWithFlag(relation, FLAG_NOT_NEG)));
//            System.out.println("test: FLAG_NONE(0) and FLAG_AND(1)   -> " + (relation = getAppendedRelationWithFlag(relation, FLAG_OR)));
//            System.out.println("test: FLAG_NONE(0) and FLAG_AND(1)   -> " + (relation = getAppendedRelationWithFlag(relation, FLAG_OR)));

        }

    }

}
