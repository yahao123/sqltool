package com.wyh.sqltool;

import java.awt.LayoutManager;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

public class Main extends JFrame implements ActionListener {
    /**
     * 获取参数
     */
    private static Pattern PARAM_PATTERN = Pattern.compile("\\?");

    private JPanel jp = new JPanel();
    private JLabel[] jlArray = new JLabel[]{new JLabel("SQL"), new JLabel("字段值"), new JLabel("结果"), new JLabel("")};
    private JButton[] jbArray = new JButton[]{new JButton("执行"), new JButton("清空"), new JButton("复制")};
    private JTextField jtxtSql = new JTextField();
    private JTextField JTextValue = new JTextField();

    TextArea resultSQL = new TextArea("", 20, 43, 1);

    public Main() {
        this.jp.setLayout((LayoutManager) null);

        int i;
        for (i = 0; i < 3; ++i) {
            this.jlArray[i].setBounds(30, 20 + i * 50, 80, 26);
            this.jbArray[i].setBounds(120 + i * 110, 660, 80, 26);
            this.jp.add(this.jlArray[i]);
            this.jp.add(this.jbArray[i]);
            this.jbArray[i].addActionListener(this);
        }

        for (i = 2; i < 4; ++i) {
            this.jlArray[i].setBounds(30, 20 + i * 50, 80, 26);
            this.jp.add(this.jlArray[i]);
        }

        this.jtxtSql.setBounds(115, 20, 500, 30);
        this.jp.add(this.jtxtSql);
        this.jtxtSql.addActionListener(this);
        this.JTextValue.setBounds(115, 70, 500, 30);
        this.jp.add(this.JTextValue);
        this.JTextValue.addActionListener(this);
        this.resultSQL.setBounds(115, 120, 500, 500);
        this.jp.add(this.resultSQL);
        this.jlArray[3].setBounds(10, 250, 700, 30);
        this.jp.add(this.jlArray[3]);
        this.add(this.jp);
        this.setDefaultCloseOperation(3);
        this.setTitle("小马哥工具2.1版");
        this.setResizable(false);
        this.setBounds(100, 230, 700, 800);
        this.setVisible(true);
    }


    /**
     * @description: 操作的入口
     * @param: ActionEvent
     * @return:
     * @author XiaoMage
     * @time: 2020-10-16 09:58
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.jtxtSql) {
            this.JTextValue.requestFocus();
        } else if (e.getSource() == this.jbArray[0]) {
            //执行操作
            String strSql;
            if (this.jtxtSql.getText().equals("") && String.valueOf(this.JTextValue.getText()).equals("")) {
                if ("".equals(this.resultSQL.getText())) {
                    this.resultSQL.setText("参数不能为空!,请重新参数!");
                } else {
                    strSql = clearInvalidPrefix(this.resultSQL.getText());
                    if (strSql.indexOf("Parameters:") != -1) {
                        String[] strArray = strSql.split("\r\n");
                        if (strArray.length == 1) {
                            strArray = strSql.split("\\[" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE).substring(0, 4) + "-");
                        }

                        String str1 = strArray[0];
                        String str2 = "";

                        for (int i = 1; i < strArray.length; ++i) {
                            str2 = str2 + strArray[i];
                        }

                        String[] strArray1 = str2.split("Parameters:");
                        String str22 = strArray1[1];
                        String resultStr = getResultSQL(str1, str22);
                        this.resultSQL.setText(resultStr);
                    }
                }
            } else {
                try {
                    System.out.println(this.jtxtSql.getText());
                    System.out.println(this.JTextValue.getText());
                    strSql = getResultSQL(this.jtxtSql.getText(), this.JTextValue.getText());
                    this.resultSQL.setText(strSql);
                } catch (Exception var9) {
                    System.out.println(var9.getMessage());
                    this.resultSQL.setText("参数错误,请检查参数!");
                }
            }
        } else if (e.getSource() == this.jbArray[1]) {
            //清空操作
            this.jlArray[2].setText("结果");
            this.jtxtSql.setText("");
            this.JTextValue.setText("");
            this.resultSQL.setText("");
            this.jtxtSql.requestFocus();
        } else if (e.getSource() == this.jbArray[2]) {
            //复制操作
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(this.resultSQL.getText());
            clip.setContents(tText, null);
        }

    }

    public static void main(String[] args) {
        new Main();
    }

    public static String getResultSQL(String text, String str) {
        str = str.replace("(Integer)", "(String)");
        StringBuffer buffer = new StringBuffer();
        String[] words = ClearBracket(str).split(",");

        for (int i = 0; i < words.length; ++i) {
            Matcher matcher = PARAM_PATTERN.matcher(text);

            while (matcher.find()) {
                matcher.appendReplacement(buffer, "'" + words[i++].trim() + "'");
            }
            matcher.appendTail(buffer);
        }

        String resultSQL = buffer.toString().replace("'null'", "null");
        if (resultSQL.indexOf(" limit") != -1) {
            StringBuffer str1 = new StringBuffer();
            String[] strArray = resultSQL.split("limit");

            for (int i = 0; i < strArray.length; ++i) {
                if (i == strArray.length - 1) {
                    str1.append(strArray[i].replace("'", ""));
                } else {
                    str1.append(strArray[i] + " limit");
                }
            }

            return str1.toString();
        } else {
            System.out.println("最终执行结果SQL>>>>>>>>>>>>>>>>" + resultSQL);
            return resultSQL;
        }
    }


    /**
     * @description: 去掉Preparing: 之前无效的字符
     * @param: strSql
     * @return:
     * @author XiaoMage
     * @time: 2020-10-16 09:54
     */
    private static String clearInvalidPrefix(String strSql) {
        List<String> prefixList = Arrays.asList("PREPARING: SELECT", "PREPARING: UPDATE", "PREPARING: DELETE", "PREPARING: INSERT");
        String strUpperCase = strSql.toUpperCase();
        Iterator var3 = prefixList.iterator();
        String prefix;
        do {
            if (!var3.hasNext()) {
                return strSql;
            }
            prefix = (String) var3.next();
        } while (!strUpperCase.contains(prefix));
        return strSql.substring(strUpperCase.indexOf(prefix) + 10, strUpperCase.length());
    }

    private static String ClearBracket(String context) {
        String pattern = "\\([^)]*\\)";
        context = context.replaceAll(pattern, "");
        return context;
    }
}
