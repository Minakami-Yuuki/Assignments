package com.example.calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // 操作数
    private Button num0, num1, num2, num3, num4, num5, num6, num7, num8, num9;

    // 操作码
    private Button plus_btn, subtract_btn, multiply_btn, divide_btn, percent_btn, equal_btn, sin_btn, cos_btn, switch_btn, fac_btn;

    // 初始化 - 删除 - 取点
    private Button pot_btn, delete_btn, ac_btn;

    // 结果存储
    private EditText etResultText;

    // 当前已输入字符
    private String existedText = "";

    // 判断是否已进行操作计算 (包括特殊运算)
    private boolean isCounted = false;

    // 以负号开头，且运算符不是是减号
    // 如：-21×2
    private boolean startWithSubtract = false;

    // 以负号开头，且运算符是减号
    // 如：-21-2
    private boolean bothWithSubtract = false;

    // 不以负号开头，且包含运算符
    // 如：21÷2
    private boolean noStartWithSubtract = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 控件初始化
        initView();
        initEvent();

    }

    private void initView() {

        // 操作数
        num0 = (Button) findViewById(R.id.num_zero);
        num1 = (Button) findViewById(R.id.num_one);
        num2 = (Button) findViewById(R.id.num_two);
        num3 = (Button) findViewById(R.id.num_three);
        num4 = (Button) findViewById(R.id.num_four);
        num5 = (Button) findViewById(R.id.num_five);
        num6 = (Button) findViewById(R.id.num_six);
        num7 = (Button) findViewById(R.id.num_seven);
        num8 = (Button) findViewById(R.id.num_eight);
        num9 = (Button) findViewById(R.id.num_nine);

        // 操作码
        plus_btn = (Button) findViewById(R.id.plus_btn);
        subtract_btn = (Button) findViewById(R.id.subtract_btn);
        multiply_btn = (Button) findViewById(R.id.multiply_btn);
        divide_btn = (Button) findViewById(R.id.divide_btn);
        equal_btn = (Button) findViewById(R.id.equal_btn);
        percent_btn = (Button) findViewById(R.id.percent_btn);
        sin_btn = (Button) findViewById(R.id.sin_btn);
        cos_btn = (Button) findViewById(R.id.cos_btn);
        fac_btn = (Button) findViewById(R.id.fac_btn);
        switch_btn = (Button) findViewById(R.id.switch_btn);

        // 剩余字符
        pot_btn = (Button) findViewById(R.id.pot_btn);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        ac_btn = (Button) findViewById(R.id.ac_btn);

        // 结果
        etResultText = (EditText) findViewById(R.id.result_text);

        // 已输入字符
        existedText = etResultText.getText().toString();

    }

    // 获取控件映射
    private void initEvent() {
        num0.setOnClickListener(this);
        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);
        num5.setOnClickListener(this);
        num6.setOnClickListener(this);
        num7.setOnClickListener(this);
        num8.setOnClickListener(this);
        num9.setOnClickListener(this);

        plus_btn.setOnClickListener(this);
        subtract_btn.setOnClickListener(this);
        multiply_btn.setOnClickListener(this);
        divide_btn.setOnClickListener(this);
        equal_btn.setOnClickListener(this);
        sin_btn.setOnClickListener(this);
        cos_btn.setOnClickListener(this);
        fac_btn.setOnClickListener(this);
        switch_btn.setOnClickListener(this);

        pot_btn.setOnClickListener(this);
        percent_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        ac_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            // 操作数 (进行等号点击判断)
            case R.id.num_zero:
                existedText = isOver(existedText,"0");
                break;
            case R.id.num_one:
                existedText = isOver(existedText,"1");
                break;
            case R.id.num_two:
                existedText = isOver(existedText,"2");
                break;
            case R.id.num_three:
                existedText = isOver(existedText,"3");
                break;
            case R.id.num_four:
                existedText = isOver(existedText,"4");
                break;
            case R.id.num_five:
                existedText = isOver(existedText,"5");
                break;
            case R.id.num_six:
                existedText = isOver(existedText,"6");
                break;
            case R.id.num_seven:
                existedText = isOver(existedText,"7");
                break;
            case R.id.num_eight:
                existedText = isOver(existedText,"8");
                break;
            case R.id.num_nine:
                existedText = isOver(existedText,"9");
                break;

            // 加法运算:
            case R.id.plus_btn:

                // 错误语句判断
                // 若输出了错误语句 则所有操作符(除删除操作符)均失效
                if(mistakeJudge()){}

                else {
                    // 判断表达式是否可以进行计算
                    // 若先前有符号位和两个数值 则先计算再添加符号
                    // 若先前只有一个数值 则先添加符号

                    // 符合运算操作时 (即两个操作数时)
                    if (judgeExpression()) {
                        existedText = getResult();
                        existedText += "+";
                    }

                    else {
                        // 计算后自动设等号为未点击
                        if (isCounted) {
                            isCounted = false;
                        }

                        // 只有单个数时
                        // 确保连续按操作数时不会出现多个操作数
                        if ((existedText.substring(existedText.length() - 1)).equals("-")) {
                            existedText = existedText.replace("-", "+");
                        } else if ((existedText.substring(existedText.length() - 1)).equals("×")) {
                            existedText = existedText.replace("×", "+");
                        } else if ((existedText.substring(existedText.length() - 1)).equals("÷")) {
                            existedText = existedText.replace("÷", "+");
                        } else if (!(existedText.substring(existedText.length() - 1)).equals("+")) {
                            existedText += "+";
                        }

                    }
                }
                break;

            // 减法运算:
            case R.id.subtract_btn:

                if(mistakeJudge()){}

                else {
                    if (judgeExpression()) {
                        existedText = getResult();
                        existedText += "-";
                    }
                    else {

                        if (isCounted) {
                            isCounted = false;
                        }

                        if ((existedText.substring(existedText.length() - 1)).equals("+")) {
                            existedText = existedText.replace("+", "-");
                        } else if ((existedText.substring(existedText.length() - 1)).equals("×")) {
                            existedText = existedText.replace("×", "-");
                        } else if ((existedText.substring(existedText.length() - 1)).equals("÷")) {
                            existedText = existedText.replace("÷", "-");
                        } else if (!(existedText.substring(existedText.length() - 1)).equals("-")) {
                            existedText += "-";
                        }
                    }
                }
                break;

            // 乘法运算:
            case R.id.multiply_btn:

                if(mistakeJudge()){}

                else {
                    if (judgeExpression()) {
                        existedText = getResult();
                        existedText += "×";
                    }
                    else {

                        if (isCounted) {
                            isCounted = false;
                        }

                        if ((existedText.substring(existedText.length() - 1)).equals("+")) {
                            existedText = existedText.replace("+", "×");
                        } else if ((existedText.substring(existedText.length() - 1)).equals("-")) {
                            existedText = existedText.replace("-", "×");
                        } else if ((existedText.substring(existedText.length() - 1)).equals("÷")) {
                            existedText = existedText.replace("÷", "×");
                        } else if (!(existedText.substring(existedText.length() - 1)).equals("×")) {
                            existedText += "×";
                        }
                    }
                }
                break;

            // 除法运算:
            case R.id.divide_btn:

                if(mistakeJudge()){}

                else{
                    if (judgeExpression()) {
                        existedText = getResult();
                        existedText += "÷";
                    }
                    else {

                        if (isCounted) {
                            isCounted = false;
                        }

                        if ((existedText.substring(existedText.length() - 1)).equals("+")) {
                            existedText = existedText.replace("+", "÷");
                        } else if ((existedText.substring(existedText.length() - 1)).equals("-")) {
                            existedText = existedText.replace("-", "÷");
                        } else if ((existedText.substring(existedText.length() - 1)).equals("×")) {
                            existedText = existedText.replace("×", "÷");
                        } else if (!(existedText.substring(existedText.length() - 1)).equals("÷")) {
                            existedText += "÷";
                        }
                    }
                }
                break;

            // 点击等号时出结果:
            // 并设置为已点击等号(isCounted)
            case R.id.equal_btn:
                existedText = getResult();
                isCounted = true;
                break;

            // 小数点:
            case R.id.pot_btn:

                // 所添加的错误判断：
                if(mistakeJudge()) {}

                else {
                    // 未运算过(未点击等号):
                    if (!isCounted){

                        // 拥有运算式时
                        if (existedText.contains("+") || existedText.contains("-") ||
                                existedText.contains("×") || existedText.contains("÷") ){

                            String param1 = null;
                            String param2 = null;

                            if (existedText.contains("+")) {
                                param1 = existedText.substring(0, existedText.indexOf("+"));
                                param2 = existedText.substring(existedText.indexOf("+") + 1);
                            }
                            else if (existedText.contains("-")) {
                                param1 = existedText.substring(0, existedText.indexOf("-"));
                                param2 = existedText.substring(existedText.indexOf("-") + 1);
                            }
                            else if (existedText.contains("×")) {
                                param1 = existedText.substring(0, existedText.indexOf("×"));
                                param2 = existedText.substring(existedText.indexOf("×") + 1);
                            }
                            else if (existedText.contains("÷")) {
                                param1 = existedText.substring(0, existedText.indexOf("÷"));
                                param2 = existedText.substring(existedText.indexOf("÷") + 1);
                            }

                            // 判断后操作数是否为小数
                            assert param2 != null;
                            boolean isContainedPoint = param2.contains(".");

                            // 确保不会出现多个小数点的情况
                            // 没有小数点时
                            if (!isContainedPoint){
                                // 若第二操作数为空
                                if (param2.equals("")){
                                    existedText = "无法添加小数点!";
                                    setToast(MainActivity.this, existedText);
                                    break;
                                }
                                // 若第二操作数不为空
                                // 则最多只能添加1个小数点
                                else {
                                    existedText += ".";
                                }
                            }
                            // 若已包含小数点则不做操作
                        }

                        // 仅有单个数时
                        else {

                            boolean isContainedPoint = existedText.contains(".");

                            // 不包含小数点 (即可添加小数点)
                            if (!isContainedPoint) {
                                if(existedText.equals("无法添加小数点!"))
                                    break;
                                existedText += ".";
                            }
                            // 若已包含小数点则不做操作
                        }
                        isCounted = false;

                    }

                    else {
                        // 已经出结果
                        boolean isContainedPoint = existedText.contains(".");

                        // 不含小数点时 最多添加1个小数点
                        if(!isContainedPoint)
                            existedText += ".";
                        // 若已包含小数点则不做操作
                        isCounted = false;
                    }
                }
                break;

            // 取余操作:
            case R.id.percent_btn:

                // 若为错误操作 则均不做操作
                if(mistakeJudge()){}

                else {

                    // 将算式带入三个算术操作式中
                    getCondition();

                    // 判断是否为符合操作的运算 (非单个数)
                    if (startWithSubtract || bothWithSubtract || noStartWithSubtract) {
                        // 若符合运算规则 则需要得出结果才能够取余 (即按等号)
                        if(existedText.substring(existedText.length()-1).equals("+") ||
                           existedText.substring(existedText.length()-1).equals("-") ||
                           existedText.substring(existedText.length()-1).equals("×") ||
                           existedText.substring(existedText.length()-1).equals("÷")) {
                                // 非法取余操作
                                existedText = "无法取余!";
                            setToast(MainActivity.this, existedText);
                        }
                    }

                    // 仅含有前操作数
                    else {
                        // 若为0 则无法取余
                        if (existedText.equals("0")) {
                            return;
                        }

                        // 若不为0 则 ( / 100) 取余
                        else {
                            double temp = Double.parseDouble(existedText);
                            existedText = String.valueOf(temp / 100);
                            isCounted = true;
                        }
                    }
                }
                break;

            // 取sin：
            case R.id.sin_btn:

                if(mistakeJudge()){}

                else {
                    // 判断是否满足运算式
                    getCondition();

                    // 判断是否为符合操作的运算 (非单个数)
                    if (startWithSubtract || bothWithSubtract || noStartWithSubtract) {
                        // 若符合运算规则 则需要得出结果才能够取余 (即按等号)
                        if(existedText.substring(existedText.length()-1).equals("+") ||
                                existedText.substring(existedText.length()-1).equals("-") ||
                                existedText.substring(existedText.length()-1).equals("×") ||
                                existedText.substring(existedText.length()-1).equals("÷")) {
                            // 非法取余操作
                            existedText = "无法取sin!";
                            setToast(MainActivity.this, existedText);
                            isCounted = true;
                        }
                    }

                    // 仅只有单个操作数时
                    else {
                        double temp = Double.parseDouble(existedText);
                        // 弧度转度数
                         temp = Math.toRadians(temp);
                        existedText = String.valueOf(Math.sin(temp));
                        isCounted = true;
                    }
                }
                break;

            // 取cos：
            case R.id.cos_btn:

                if(mistakeJudge()){}

                else{
                    // 判断是否满足运算式
                    getCondition();

                    // 判断是否为符合操作的运算 (非单个数)
                    if (startWithSubtract || bothWithSubtract || noStartWithSubtract) {
                        // 若符合运算规则 则需要得出结果才能够取余 (即按等号)
                        if(existedText.substring(existedText.length()-1).equals("+") ||
                                existedText.substring(existedText.length()-1).equals("-") ||
                                existedText.substring(existedText.length()-1).equals("×") ||
                                existedText.substring(existedText.length()-1).equals("÷")) {
                            // 非法取余操作
                            existedText = "无法取cos!";
                            setToast(MainActivity.this, existedText);
                            isCounted = true;
                        }
                    }

                    // 仅只有单个操作数时
                    else {
                        double temp = Double.parseDouble(existedText);
                        // 弧度转度数
                         temp = Math.toRadians(temp);
                        existedText = String.valueOf(Math.cos(temp));

                        isCounted = true;
                    }
                }
                break;

            // 取阶乘：
            case R.id.fac_btn:

                if(mistakeJudge()){}

                else {
                    // 将算式带入三个算术操作式中
                    getCondition();

                    // 判断是否为符合操作的运算 (非单个数)
                    if (startWithSubtract || bothWithSubtract || noStartWithSubtract) {
                        // 若符合运算规则 则需要得出结果才能够取余 (即按等号)
                        if(existedText.substring(existedText.length()-1).equals("+") ||
                                existedText.substring(existedText.length()-1).equals("-") ||
                                existedText.substring(existedText.length()-1).equals("×") ||
                                existedText.substring(existedText.length()-1).equals("÷")) {
                            // 非法取余操作
                            existedText = "无法计算阶乘!";
                            setToast(MainActivity.this, existedText);
                            isCounted = true;
                        }
                    }
                    else {
                        // 若不为负数
                        if(!existedText.contains("-")) {
                            int temp = Integer.parseInt(existedText);
                            int fac = 1;

                            for (int i = 1; i <= temp; i++) {
                                fac *= i;
                            }

                            existedText = String.valueOf(fac);
                            isCounted = true;

                        }
                        // 若为负数 (无法计算阶乘)
                        else {
                            existedText = "阶乘无法计算负数!";
                            setToast(MainActivity.this, existedText);
                            isCounted = true;
                        }
                    }
                }
                break;

            // 正负号交换：
            case R.id.switch_btn:

                if(mistakeJudge()){}

                else {

                    if(existedText.equals("0")) {}

                    else {
                        // 带有负号时 去除负号
                        if(existedText.contains("-")) {
                            existedText = existedText.replace("-", "");
                        }
                        // 没有负号时 加上负号
                        else {
                            existedText = existedText.replaceFirst("","-");
                        }
                    }
                }
                break;


            // 单值删除:
            case R.id.delete_btn:

                // 若为error 则直接为0
                if (existedText.equals("error")){
                    existedText = "0";
                }
                // 长度大于0
                else if (existedText.length() > 0){
                    // 若仅为一个数 则直接归0
                    if (existedText.length() == 1) {
                        existedText = "0";
                    }
                    // 否则向前位移一位
                    else {
                        existedText = existedText.substring(0,existedText.length()-1);
                    }
                }
                break;

            // 整体清除:
            case R.id.ac_btn:
                existedText = "0";
                break;
        }

        // 结果显示于EditText窗口
        etResultText.setText(existedText);
    }

    // 获取结果方法
    @SuppressLint("DefaultLocale")
    private String getResult() {

        String tempResult = null;

        String param1, param2;

        double arg1, arg2, result;

        // 判断条件
        getCondition();

        // 若已存在符合条件的运算式 则可以直接计算
        if ( startWithSubtract || noStartWithSubtract || bothWithSubtract) {

            if (existedText.contains("+")) {

                // 获取操作数
                param1 = existedText.substring(0, existedText.indexOf("+"));
                param2 = existedText.substring(existedText.indexOf("+") + 1);

                // 若第二个操作数为空 则直接显示为第一个操作数
                if (param2.equals("")) {
                    tempResult = existedText.substring(0, existedText.length() - 1);
                } else {
                    // 强转计算 并格式化输出
                    arg1 = Double.parseDouble(param1);
                    arg2 = Double.parseDouble(param2);
                    result = arg1 + arg2;
                    tempResult = String.format("%f", result);
                    // 采用正则表达式 消除多余0和非法输出
                    tempResult = subZeroAndDot(tempResult);
                }
            }

            else if (existedText.contains("×")) {

                param1 = existedText.substring(0, existedText.indexOf("×"));
                param2 = existedText.substring(existedText.indexOf("×") + 1);

                if (param2.equals("")) {
                    tempResult = existedText.substring(0, existedText.length() - 1);
                } else {
                    arg1 = Double.parseDouble(param1);
                    arg2 = Double.parseDouble(param2);
                    result = arg1 * arg2;
                    tempResult = String.format("%f", result);
                    tempResult = subZeroAndDot(tempResult);
                }
            }

            else if (existedText.contains("÷")) {

                param1 = existedText.substring(0, existedText.indexOf("÷"));
                param2 = existedText.substring(existedText.indexOf("÷") + 1);

                // 除数为0 非法操作
                if (param2.equals("0")){
                    tempResult = "除数不能为0!";
                    setToast(MainActivity.this, tempResult);
                } else if (param2.equals("")){
                    tempResult = existedText.substring(0, existedText.length() - 1);
                } else {
                    arg1 = Double.parseDouble(param1);
                    arg2 = Double.parseDouble(param2);
                    result = arg1 / arg2;
                    tempResult = String.format("%f", result);
                    tempResult = subZeroAndDot(tempResult);
                }
            }

            else if (existedText.contains("-")) {

                // 负数开头 避免不同的 '-' 被误用
                param1 = existedText.substring(0, existedText.lastIndexOf("-"));
                param2 = existedText.substring(existedText.lastIndexOf("-") + 1);

                if (param2.equals("")){
                    tempResult = existedText.substring(0, existedText.length() - 1);
                } else {
                    arg1 = Double.parseDouble(param1);
                    arg2 = Double.parseDouble(param2);
                    result = arg1 - arg2;
                    tempResult = String.format("%f", result);
                    tempResult = subZeroAndDot(tempResult);
                }
            }
        }

        // 若不是标准形式(为单个数 或 直接点击符号) 则直接显示为第一个操作数
        else {
            tempResult = existedText;
        }

        return tempResult;
    }

    // 判断是否点击过特殊运算符
    // 包括：等号 取余 正负号转换 sin cos 阶乘
    private String isOver(String existedText, String s) {

        // 若没按过 则等待下一个数的输入
        if (!isCounted){

            // 清空开头的0
            if (existedText.equals("0")){
                existedText = "";
            }

            existedText += s;
        }

        // 若已经按过
        // 则自动变为下一个数进行显示
        else {
            existedText = s;
            isCounted = false;
        }

        return existedText;
    }

    // 使用正则表达式
    // 若运算式中包含小数点
    // 则自动消除多余0和非法结尾小数点
    public static String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");   //去掉多余的0
            s = s.replaceAll("[.]$", "");   //如最后一位是.则去掉
        }

        return s;
    }

    // 判断表达式类型 (是 单个操作数 还是 单个操作数 + 操作符 还是 两个操作数)
    private boolean judgeExpression() {

        getCondition();

        String tempParam2;

        if ( startWithSubtract || noStartWithSubtract || bothWithSubtract ) {

            if (existedText.contains("+")) {

                // 获取第二操作数
                tempParam2 = existedText.substring(existedText.indexOf("+") + 1);

                // 若第二操作数为空 则正常操作不成立
                if (tempParam2.equals("")) {
                    return false;
                }
                else {
                    return true;
                }
            }

            else if (existedText.contains("×")) {

                tempParam2 = existedText.substring(existedText.indexOf("×") + 1);

                if (tempParam2.equals("")) {
                    return false;
                }
                else {
                    return true;
                }
            }

            else if (existedText.contains("÷")) {

                tempParam2 = existedText.substring(existedText.indexOf("÷") + 1);

                if (tempParam2.equals("")) {
                    return false;
                }
                else {
                    return true;
                }
            }

            // 双负号时需要注意: 要保留前操作数的符号, 则需要在最后一个 '-' 处取第二操作数
            else if (existedText.contains("-")) {

                tempParam2 = existedText.substring(existedText.lastIndexOf("-") + 1);

                if (tempParam2.equals("")) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
        return false;
    }

    // 三种合法执行条件：
    private void getCondition() {
        // 检测是否以指定符号开头

        // 1.以负号开头, 且运算符不是减号
        startWithSubtract = existedText.startsWith("-") && ( existedText.contains("+") ||
                existedText.contains("×") || existedText.contains("÷") );

        // 2.以负号开头, 且运算符是减号
        bothWithSubtract = existedText.startsWith("-") && ( existedText.lastIndexOf("-") != 0 );

        // 3.非负号开头, 且含有运算符
        noStartWithSubtract = !existedText.startsWith("-") && ( existedText.contains("+") ||
                existedText.contains("-") || existedText.contains("×") || existedText.contains("÷"));
    }

    public void setToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean mistakeJudge() {
        // 若为错误操作 则均不做操作
        if (existedText.equals("无法添加小数点!") ||
            existedText.equals("除数不能为0!") ||
            existedText.equals("无法取余!") ||
            existedText.equals("无法取sin!") ||
            existedText.equals("无法取cos!") ||
            existedText.equals("无法计算阶乘!") ||
            existedText.equals("阶乘无法计算负数!")) {
                return true;
        }
        else
            return false;
    }

}