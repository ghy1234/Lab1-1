//in branch C4, modify samples
package com.se.lab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
//import java.io.*;
//try to use git

public class Polynomial {

	static ArrayList<Monomial> poly;
	
	public static void main(String[] args) {
		
		boolean stay = true;
		Scanner scan = new Scanner(System.in);
		
		//test huge file input
		//try{scan=new Scanner(new FileInputStream("input.txt"));}catch(Exception e){}
		//Caculate time
		//long totTime = 0;
		//long STime = System.currentTimeMillis();
		while(stay){
			
			String line;
			line = scan.nextLine();
			
			while(line.equals("")) line = scan.nextLine();

			if(line.equals("!bye")){
				stay = false;
			}else if(line.charAt(0) == '!'){
				//long startTime = System.currentTimeMillis();
				command(line);
				//long endTime = System.currentTimeMillis();
				
				//System.out.println("Command StartTime = " + startTime);
				//System.out.println("Command EndTime = " + endTime);
				//System.out.println("Command UseTime = " + (float)(endTime-startTime)/1000.0 + 's');
				//totTime+=endTime-startTime;
			}else{
				//long startTime = System.currentTimeMillis();
				poly = expression(line);			
				//long endTime = System.currentTimeMillis();
				
				//System.out.println("Expression StartTime = " + startTime);
				//System.out.println("Expression EndTime = " + endTime);
				//System.out.println("Expression UseTime = " + (float)(endTime-startTime)/1000.0 + 's');
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
	 * 解析命令并调用化简和求导函数
	 * @param s
	 */
	public static void command(String s)
	{
		s = s.substring(1).trim();
		boolean iscommand = false;
		
		if(s.startsWith("d/d"))
		{
			iscommand=true;
			s = s.substring(3).trim();
			derivative(poly,s);
		}
		else
		{
			HashMap<String, Float> map = new HashMap<String, Float>();
			iscommand = true;
			if(s.startsWith("simplify"))
			{
				s = s.substring(8).trim();
				while(!s.equals(""))	
				{
					int space_pos = s.indexOf(" ");
					if(space_pos == -1)
						space_pos = s.length();
					
					int equal_pos = s.indexOf("=");
					if(equal_pos == -1)
						equal_pos = s.length();
					
					if(equal_pos<space_pos)
					{
						if(equal_pos>=s.length()-1)
						{
							iscommand = false;
							break;
						}
						String var = s.substring(0, equal_pos).trim();
						String value = s.substring(equal_pos+1, space_pos).trim();
						char[] Cvalue = value.toCharArray();
						for(char i : Cvalue)
							if(i!='-' && i!='.' && (i<'0' || i>'9'))
							{
								iscommand = false;
								break;
							}
						float a;
						try{
							a = Float.parseFloat(value);
						}catch(NumberFormatException e){
							System.out.println("Command ERROR!");
							return;
						}
						map.put(var, a);
						if(space_pos!=s.length())
							s=s.substring(space_pos+1);
						else
							break;
					}
					else
					{
						iscommand = false;
						break;
					}
				}
			}
			else
			{
				iscommand = false;
			}
			
			if(iscommand == true)
			{
				simplify(poly, map);
			}
			else
			{
				System.out.println("Command ERROR!");
			}
		}
	}
	/**
	 * 读入字符串并转化为表达式,并输出表达式或错误
	 * @param s 输入表达式
	 * @return 多项式
	 */
	public static ArrayList<Monomial> expression(String s){
		final int INPUT_NUM = 0;
		final int INPUT_VAR = 1;
		final int INPUT_MUL = 2;
		final int INPUT_NEW = 3;
		final int INPUT_POW = 4;
		int state = INPUT_NEW;
		int temp_num = 0;
		String temp_var = "";
		Monomial mono = new Monomial();
		ArrayList<Monomial> poly = new ArrayList<Monomial>();
		
		int i = 0;
		if(s.charAt(0) == '-'){
			mono.k *= -1;
			i++;
		}
		
		for(; i < s.length(); i++){
			char ch = s.charAt(i);
			if(ch == ' ' || ch == '\t') continue;
			switch(state){
			case INPUT_NUM:
				if(ch == '*'){
					mono.k *= temp_num;
					temp_num = 0;
					state = INPUT_MUL;
				}else if(ch >= '0' && ch <= '9'){
					temp_num = temp_num * 10 + (ch - '0');
				}else if(ch == '+' || ch == '-'){
					mono.k *= temp_num;
					temp_var = "";
					temp_num = 0;
					poly.add(mono);
					mono = new Monomial();
					state = INPUT_NEW;
					if(ch == '-'){
						mono.k *= -1;
					}
				}else if((ch>='a' && ch<='z') || (ch <= 'A' && ch >= 'Z')){
					mono.k *= temp_num;
					temp_num = 0;
					temp_var = ch + "";
					state = INPUT_VAR;
				}else{
					System.out.println("Syntax error!");
					return null;
				}
				break;
			case INPUT_VAR:
				if((ch>='a' && ch<='z') || (ch <= 'A' && ch >= 'Z')){
					temp_var = temp_var + ch;
				}else if(ch == '+' || ch == '-'){
					if(mono.variable.containsKey(temp_var)) mono.variable.put(temp_var, mono.variable.get(temp_var) + 1);
					else mono.variable.put(temp_var, 1);
					temp_var = "";
					temp_num = 0;
					poly.add(mono);
					mono = new Monomial();
					state = INPUT_NEW;
					if(ch == '-'){
						mono.k *= -1;
					}
				}else if(ch == '*'){
					if(mono.variable.containsKey(temp_var)) mono.variable.put(temp_var, mono.variable.get(temp_var) + 1);
					else mono.variable.put(temp_var, 1);
					temp_var = "";
					state = INPUT_MUL;
				}else if(ch == '^'){
					state = INPUT_POW;
				}else{
					System.out.println("Syntax error!");
					return null;
				}
				break;
			case INPUT_MUL:
				if(ch >= '0' && ch <= '9'){
					temp_num = ch - '0';
					state = INPUT_NUM;
				}else if((ch>='a' && ch<='z') || (ch <= 'A' && ch >= 'Z')){
					temp_var = "" + ch;
					state = INPUT_VAR;
				}else{
					System.out.println("Syntax error!");
					return null;					
				}
				break;
			case INPUT_NEW:
				if(ch >= '0' && ch <= '9'){
					temp_num = temp_num * 10 + (ch - '0');
					state = INPUT_NUM;
				}else if((ch>='a' && ch<='z') || (ch <= 'A' && ch >= 'Z')){
					temp_var = temp_var + ch;
					state = INPUT_VAR;
				}else{
					System.out.println("Syntax error!");
					return null;
				}
				break;
			case INPUT_POW:
				if(ch == '*' || ch == '+' || ch == '-'){
					if(temp_num == 0){
						System.out.println("Syntax error!");
						return null;
					}

					Integer curPow = mono.variable.get(temp_var);
					int cur = curPow==null?0:curPow;
					mono.variable.put(temp_var, temp_num + cur);
					if(ch != '*'){
						temp_var = "";
						temp_num = 0;
						poly.add(mono);
						mono = new Monomial();
						state = INPUT_NEW;
						if(ch == '-'){
							mono.k *= -1;
						}
					}else{
						temp_var = "";
						temp_num = 0;
						state = INPUT_MUL;
					}
				}else if(ch >= '0' && ch <= '9'){
					temp_num = temp_num * 10 + (ch - '0');
				}else{
					System.out.println("Syntax error!");
					return null;
				}
				break;
			default:
				return null;
			}
		}
		switch(state){
		case INPUT_NEW:
		case INPUT_MUL:
			System.out.println("Syntax error!");
			return null;
		case INPUT_VAR:
			if(mono.variable.containsKey(temp_var)) mono.variable.put(temp_var, mono.variable.get(temp_var) + 1);
			else mono.variable.put(temp_var, 1);
			break;
		case INPUT_NUM:
			mono.k *= temp_num;
			break;
		case INPUT_POW:
			if(temp_num == 0){
				System.out.println("Syntax error!");
				return null;
			}
			Integer curPow = mono.variable.get(temp_var);
			int cur = curPow==null?0:curPow;
			mono.variable.put(temp_var, temp_num + cur);
		}
		poly.add(mono);
		poly = merge(poly);
		printPoly(poly);
		return poly;
	}
	
	/**
	 * 将多项式进行带入化简，并输出结果表达式或错误
	 * @param poly 多项式
	 * @param map 代入的变量及其值
	 */
	public static void simplify(ArrayList<Monomial> poly, HashMap<String, Float> map)
	{		
		ArrayList<Monomial> newpoly = new ArrayList<Monomial>();
		for(Monomial i:poly)
		{
			Monomial j = new Monomial(i);
			newpoly.add(j);
		}
		
		for(String variable:map.keySet())
		{
			//System.out.println("TEST+"+variable+"+");
			boolean appear = false;
			for(int i = 0; i < newpoly.size(); i++)
			{
				Monomial mono = newpoly.get(i);
			
				if(mono.variable.containsKey(variable))
				{
					appear = true;
					int value = mono.variable.get(variable);
					for(int tims = 1; tims<=value; tims++)
						mono.k*=map.get(variable);
					mono.variable.remove(variable);
				}
			}
			if(appear == false)
			{
				System.out.println("Variable not found!");
				return;
			}
		}
		printPoly(newpoly);
	}
	
	/**
	 * 将多项式进行求导运算
	 * @param poly 多项式
	 * @param variable 求导变量
	 */
	public static void derivative(ArrayList<Monomial> poly, String variable)
	{
		//copy the arraylist
		boolean variable_appear = false;
		ArrayList<Monomial> newpoly = new ArrayList<Monomial>();
		for(Monomial i:poly)
		{
			Monomial j = new Monomial(i);
			newpoly.add(j);
		}
		
		for(int i = 0; i < newpoly.size(); i++)
		{
			Monomial mono = newpoly.get(i);
			if(mono.variable.containsKey(variable))
			{
				variable_appear = true;
				mono.k*=mono.variable.get(variable);
				if( mono.variable.get(variable)-1 == 0){
					mono.variable.remove(variable);
				}else{
					mono.variable.replace(variable, mono.variable.get(variable)-1);
				}
				
			}else{
				newpoly.remove(i);
				i--;
			}
		}
		if(variable_appear==true)
			printPoly(newpoly);
		else
			System.out.println("Variable not found!");
	}

	/**
	 * 将多项式进行格式化输出
	 * @param poly 多项式
	 */
	public static void printPoly(ArrayList<Monomial> poly){
		poly = merge(poly);
		for(int i = 0; i < poly.size(); i++){
			Monomial mono = poly.get(i);
			if(mono.k > 0 && i > 0){
				System.out.print('+');
			}
			boolean flag = false;
			if(mono.k!=1 || mono.variable.isEmpty()){
				if(mono.k - (int)mono.k == 0)
					System.out.print((int)mono.k);
				else
					System.out.print(mono.k);
				flag = true;
			}
			for (HashMap.Entry<String, Integer> entry : mono.variable.entrySet()) {
				System.out.print((flag?'*':"") + entry.getKey());
				flag = true;
				if(entry.getValue() != 1){
					System.out.print('^' + "" + entry.getValue());
				}
			}
		}
		System.out.println();
	}
	
	/**
	 * 合并同类项
	 * @param poly 多项式（可能含同类项）
	 * @return 无同类项的多项式
	 */
	public static ArrayList<Monomial> merge(ArrayList<Monomial> poly){
		for(int i = 0; i < poly.size(); i++){
			Monomial mono = poly.get(i);
			boolean flag = false;
			for(int j = i+1; j < poly.size(); j++){
				if(mono.variable.equals(poly.get(j).variable)){
					poly.get(j).k += mono.k;
					flag = true;
					break;
				}
			}
			if(flag){ 
				poly.remove(mono);
				i--;
			}
		}
		return poly;
	}

}

class Monomial{
	float k; //系数
	HashMap<String, Integer> variable; //变量及其次数
	
	public Monomial(){
		k = 1;
		variable = new HashMap<String, Integer>();
	}
	
	public Monomial(Monomial mono)
	{
		k=mono.k;
		variable = new HashMap<String, Integer>(mono.variable);
	}
	
}
