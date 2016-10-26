package com.se.lab;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
//import java.io.*;

/**
 * 此处应该有注释.
 */
public class Polynomial {
    /**
     * 此处应该有注释.
     */
    public static final int THREE = 3;
    /**
     * 此处应该有注释.
     */
    public static final int EIGHT = 8;
    /**
     * 此处应该有注释.
     */
    public static final int TEN = 10;
    /**
     * 此处应该有注释.
     */
    /* default */static ArrayList<Monomial> poly;

    /**
     * 此处应该有注释.
     *
     * @param args args需注释
     */
    public static void main(final String[] args) {

        boolean stay = true;
        Scanner scan = new Scanner(System.in, "utf8");

        //test huge file input
        //try{scan=new Scanner(new FileInputStream("input.txt"));}
        // catch(Exception e){}
        //Caculate time
        //long totTime = 0;
        //long STime = System.currentTimeMillis();
        while (stay) {

            String line;
            line = scan.nextLine();

            while (line.equals("")) {
                line = scan.nextLine();
            }

            if (line.equals("!bye")) {
                stay = false;
            } else if (line.charAt(0) == '!') {
                //long startTime = System.currentTimeMillis();
                command(line);
                //long endTime = System.currentTimeMillis();

                //System.out.println("Command StartTime = " + startTime);
                //System.out.println("Command EndTime = " + endTime);
                //System.out.println("Command UseTime = "
                //+ (float)(endTime-startTime)/1000.0 + 's');
                //totTime+=endTime-startTime;
            } else {
                //long startTime = System.currentTimeMillis();
                poly = expression(line);
                //long endTime = System.currentTimeMillis();

                //System.out.println("Expression StartTime = " + startTime);
                //System.out.println("Expression EndTime = " + endTime);
                //System.out.println("Expression UseTime = "
                // + (float)(endTime-startTime)/1000.0 + 's');
                //totTime+=endTime-startTime;
            }
        }
        //long ETime = System.currentTimeMillis();
        //System.out.println("Start Time = " + STime);
        //System.out.println("End Time = " + ETime);
        //System.out.println("TOT Time = " + (float)(ETime-STime)/1000.0 + 's');
        //System.out.println("TOT Run Time = "+(float)totTime/1000.0 + 's');
        scan.close();
    }

    /**
     * 解析命令并调用化简和求导函数.
     *
     * @param longs1 s1需注释
     */
    public static void command(final String longs1) {
        String longs = longs1;
        longs = longs.substring(1).trim();
        boolean iscommand = false;

        if (longs.startsWith("d/d")) {
            iscommand = true;
            longs = longs.substring(THREE).trim();
            derivative(poly, longs);
        } else {
            HashMap<String, Float> map = new HashMap<String, Float>();
            iscommand = true;
            if (longs.startsWith("simplify")) {
                longs = longs.substring(EIGHT).trim();
                while (!longs.equals("")) {
                    int spacepos = longs.indexOf(' ');
                    if (spacepos == -1) {
                        spacepos = longs.length();
                    }

                    int equalpos = longs.indexOf('=');
                    if (equalpos == -1) {
                        equalpos = longs.length();
                    }

                    if (equalpos < spacepos) {
                        if (equalpos >= longs.length() - 1) {
                            iscommand = false;
                            break;
                        }
                        String var = longs.substring(0, equalpos).trim();
                        String value = longs.substring(equalpos + 1,
                                spacepos).trim();
                        char[] cvalue = value.toCharArray();
                        for (char i : cvalue) {
                            if (i != '-' && i != '.' && (i < '0' || i > '9')) {
                                iscommand = false;
                                break;
                            }
                        }
                        float longa;
                        try {
                            longa = Float.parseFloat(value);
                        } catch (NumberFormatException e) {
                            System.out.println("Command ERROR!");
                            return;
                        }
                        map.put(var, longa);
                        if (spacepos == longs.length()) {
                            break;
                        } else {
                            longs = longs.substring(spacepos + 1);
                        }
                    } else {
                        iscommand = false;
                        break;
                    }
                }
            } else {
                iscommand = false;
            }

            if (iscommand) {
                simplify(poly, map);
            } else {
                System.out.println("Command ERROR!");
            }
        }
    }

    /**
     * 读入字符串并转化为表达式,并输出表达式或错误.
     *
     * @param longs 输入表达式
     * @return 多项式
     */
    public static ArrayList<Monomial> expression(final String longs) {
        final int iNPUTNUM = 0;
        final int iNPUTVAR = 1;
        final int iNPUTMUL = 2;
        final int iNPUTNEW = 3;
        final int iNPUTPOW = 4;
        int state = iNPUTNEW;
        int tempnum = 0;
        String tempvar = "";
        Monomial mono = new Monomial();
        ArrayList<Monomial> poly = new ArrayList<Monomial>();

        int longi = 0;
        if (longs.charAt(0) == '-') {
            mono.longk *= -1;
            longi++;
        }

        for (; longi < longs.length(); longi++) {
            char longch = longs.charAt(longi);
            if (longch == ' ' || longch == '\t') {
                continue;
            }
            switch (state) {
                case iNPUTNUM:
                    if (longch == '*') {
                        mono.longk *= tempnum;
                        tempnum = 0;
                        state = iNPUTMUL;
                    } else if (longch >= '0' && longch <= '9') {
                        tempnum = tempnum * TEN + (longch - '0');
                    } else if (longch == '+' || longch == '-') {
                        mono.longk *= tempnum;
                        tempvar = "";
                        tempnum = 0;
                        poly.add(mono);
                        mono = new Monomial();
                        state = iNPUTNEW;
                        if (longch == '-') {
                            mono.longk *= -1;
                        }
                    } else if (longch >= 'a' && longch <= 'z'
                            || longch >= 'A' && longch <= 'Z') {
                        mono.longk *= tempnum;
                        tempnum = 0;
                        tempvar = longch + "";
                        state = iNPUTVAR;
                    } else {
                        System.out.println("Syntax error!");
                        return null;
                    }
                    break;
                case iNPUTVAR:
                    if (longch >= 'a' && longch <= 'z' || longch >= 'A' && longch <= 'Z') {
                        tempvar += longch;
                    } else if (longch == '+' || longch == '-') {
                        if (mono.variable.containsKey(tempvar)) {
                            mono.variable.put(tempvar,
                                    mono.variable.get(tempvar) + 1);
                        } else {
                            mono.variable.put(tempvar, 1);
                        }
                        tempvar = "";
                        tempnum = 0;
                        poly.add(mono);
                        mono = new Monomial();
                        state = iNPUTNEW;
                        if (longch == '-') {
                            mono.longk *= -1;
                        }
                    } else if (longch == '*') {
                        if (mono.variable.containsKey(tempvar)) {
                            mono.variable.put(tempvar,
                                    mono.variable.get(tempvar) + 1);
                        } else {
                            mono.variable.put(tempvar, 1);
                        }
                        tempvar = "";
                        state = iNPUTMUL;
                    } else if (longch == '^') {
                        state = iNPUTPOW;
                    } else {
                        System.out.println("Syntax error!");
                        return null;
                    }
                    break;
                case iNPUTMUL:
                    if (longch >= '0' && longch <= '9') {
                        tempnum = longch - '0';
                        state = iNPUTNUM;
                    } else if (longch >= 'a' && longch <= 'z'
                            || longch >= 'A' && longch <= 'Z') {
                        tempvar = "" + longch;
                        state = iNPUTVAR;
                    } else {
                        System.out.println("Syntax error!");
                        return null;
                    }
                    break;
                case iNPUTNEW:
                    if (longch >= '0' && longch <= '9') {
                        tempnum = tempnum * TEN + (longch - '0');
                        state = iNPUTNUM;
                    } else if (longch >= 'a' && longch <= 'z'
                            || longch >= 'A' && longch <= 'Z') {
                        tempvar += longch;
                        state = iNPUTVAR;
                    } else {
                        System.out.println("Syntax error!");
                        return null;
                    }
                    break;
                case iNPUTPOW:
                    if (longch == '*' || longch == '+' || longch == '-') {
                        if (tempnum == 0) {
                            System.out.println("Syntax error!");
                            return null;
                        }

                        Integer curPow = mono.variable.get(tempvar);
                        int cur = curPow == null ? 0 : curPow;
                        mono.variable.put(tempvar, tempnum + cur);
                        if (longch == '*') {
                            tempvar = "";
                            tempnum = 0;
                            state = iNPUTMUL;

                        } else {
                            tempvar = "";
                            tempnum = 0;
                            poly.add(mono);
                            mono = new Monomial();
                            state = iNPUTNEW;
                            if (longch == '-') {
                                mono.longk *= -1;
                            }
                        }
                    } else if (longch >= '0' && longch <= '9') {
                        tempnum = tempnum * TEN + (longch - '0');
                    } else {
                        System.out.println("Syntax error!");
                        return null;
                    }
                    break;
                default:
                    return null;
            }
        }
        switch (state) {
            case iNPUTNEW:
            case iNPUTMUL:
                System.out.println("Syntax error!");
                return null;
            case iNPUTVAR:
                if (mono.variable.containsKey(tempvar)) {
                    mono.variable.put(tempvar,
                            mono.variable.get(tempvar) + 1);
                } else {
                    mono.variable.put(tempvar, 1);
                }
                break;
            case iNPUTNUM:
                mono.longk *= tempnum;
                break;
            case iNPUTPOW:
                if (tempnum == 0) {
                    System.out.println("Syntax error!");
                    return null;
                }
                Integer curPow = mono.variable.get(tempvar);
                int cur = curPow == null ? 0 : curPow;
                mono.variable.put(tempvar, tempnum + cur);
                break;
            default:
                break;
        }
        poly.add(mono);
        poly = merge(poly);
        printPoly(poly);
        return poly;
    }

    /**
     * 将多项式进行带入化简，并输出结果表达式或错误.
     *
     * @param poly 多项式
     * @param map  代入的变量及其值
     */
    public static void simplify(
            final List<Monomial> poly, final HashMap<String, Float> map) {
        ArrayList<Monomial> newpoly = new ArrayList<Monomial>();

        for (Monomial i : poly) {
            Monomial longj = new Monomial(i);
            newpoly.add(longj);
        }

        for (String variable : map.keySet()) {
            //System.out.println("TEST+"+variable+"+");
            boolean appear = false;
            for (int i = 0; i < newpoly.size(); i++) {
                Monomial mono = newpoly.get(i);

                if (mono.variable.containsKey(variable)) {
                    appear = true;
                    int value = mono.variable.get(variable);
                    for (int tims = 1; tims <= value; tims++) {
                        mono.longk *= map.get(variable);
                    }
                    mono.variable.remove(variable);
                }
            }
            if (!appear) {
                System.out.println("Variable not found!");
                return;
            }
        }
        printPoly(newpoly);
    }

    /**
     * 将多项式进行求导运算.
     *
     * @param poly     多项式
     * @param variable 求导变量
     */
    public static void derivative(final ArrayList<Monomial> poly,
                                  final String variable) {
        //copy the arraylist
        boolean variableappear = false;
        ArrayList<Monomial> newpoly = new ArrayList<Monomial>();
        for (Monomial i : poly) {
            Monomial longj = new Monomial(i);
            newpoly.add(longj);
        }

        for (int i = 0; i < newpoly.size(); i++) {
            Monomial mono = newpoly.get(i);
            if (mono.variable.containsKey(variable)) {
                variableappear = true;
                mono.longk *= mono.variable.get(variable);
                if (mono.variable.get(variable) - 1 == 0) {
                    mono.variable.remove(variable);
                } else {
                    mono.variable.replace(variable,
                            mono.variable.get(variable) - 1);
                }

            } else {
                newpoly.remove(i);
                i--;
            }
        }
        if (variableappear) {
            printPoly(newpoly);
        } else {
            System.out.println("Variable not found!");
        }
    }

    /**
     * 将多项式进行格式化输出.
     *
     * @param poly1 多项式
     */
    public static void printPoly(final ArrayList<Monomial> poly1) {
        ArrayList<Monomial> poly = poly1;
        poly = merge(poly);
        for (int i = 0; i < poly.size(); i++) {
            Monomial mono = poly.get(i);
            if (mono.longk > 0 && i > 0) {
                System.out.print('+');
            }
            boolean flag = false;
            if (mono.longk != 1 || mono.variable.isEmpty()) {
                if (mono.longk - (int) mono.longk == 0) {
                    System.out.print((int) mono.longk);
                } else {
                    System.out.print(mono.longk);
                }
                flag = true;
            }
            for (HashMap.Entry<String, Integer> entry
                    : mono.variable.entrySet()) {
                System.out.print((flag ? '*' : "") + entry.getKey());
                flag = true;
                if (entry.getValue() != 1) {
                    System.out.print('^' + "" + entry.getValue());
                }
            }
        }
        System.out.println();
    }

    /**
     * 合并同类项.
     *
     * @param poly 多项式（可能含同类项）
     * @return 无同类项的多项式
     */
    public static ArrayList<Monomial> merge(final ArrayList<Monomial> poly) {
        for (int i = 0; i < poly.size(); i++) {
            Monomial mono = poly.get(i);
            boolean flag = false;
            for (int j = i + 1; j < poly.size(); j++) {
                if (mono.variable.equals(poly.get(j).variable)) {
                    poly.get(j).longk += mono.longk;
                    flag = true;
                    break;
                }
            }
            if (flag) {
                poly.remove(mono);
                i--;
            }
        }
        return poly;
    }

}

/**
 * 此处应该有注释.
 */
class Monomial {
    /**
     * 此处应该有注释.
     */
    /* default */float longk; //系数
    /**
     * 此处应该有注释.
     */
    /* default */HashMap<String, Integer> variable; //变量及其次数

    /**
     * 此处应该有注释.
     */
    Monomial() {
        longk = 1;
        variable = new HashMap<String, Integer>();
    }

    /**
     * 此处应该有注释.
     *
     * @param mono mono需注释
     */
    Monomial(final Monomial mono) {
        longk = mono.longk;
        variable = new HashMap<String, Integer>(mono.variable);
    }

}

// this is a line which do nothing
