package comlisanshuxue.lisan.jihe;

import comlisanshuxue.lisan.constants.jihechangliang;
import comlisanshuxue.lisan.dto.jihedto;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(tags = "集合")
public class newjehe {

    @PostMapping("/jihe")
    public Set<Integer> jiheyunsuan(@RequestBody jihedto j) {
        List<List<Integer>> lists = new ArrayList<>();
        lists.add(j.getA());
        lists.add(j.getB());
        lists.add(j.getC());
        lists.add(j.getD());

        String shizi = j.getShizi().replaceAll("\\s+", "");

        List<Integer> result;
        try {
            result = parseExpression(shizi, lists);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("表达式解析错误: " + e.getMessage());
        }

        return new HashSet<>(result);
    }

    private List<Integer> parseExpression(String expr, List<List<Integer>> lists) {
        int[] index = new int[]{0};
        return parseExpressionHelper(expr, lists, index);
    }

    private List<Integer> parseExpressionHelper(String expr, List<List<Integer>> lists, int[] index) {
        List<Integer> current = parseOperand(expr, lists, index);
        while (index[0] < expr.length()) {
            char op = expr.charAt(index[0]);
            if (isOperator(op)) {
                index[0]++;
                List<Integer> operand = parseOperand(expr, lists, index);
                current = applyOperator(current, op, operand);
            } else if (op == ')') {
                break;
            } else {
                throw new IllegalArgumentException("无效字符: '" + op + "'");
            }
        }
        return current;
    }

    private List<Integer> parseOperand(String expr, List<List<Integer>> lists, int[] index) {
        if (index[0] >= expr.length()) {
            throw new IllegalArgumentException("表达式不完整");
        }
        char c = expr.charAt(index[0]);
        if (c == '(') {
            index[0]++;
            List<Integer> result = parseExpressionHelper(expr, lists, index);
            if (index[0] >= expr.length() || expr.charAt(index[0]) != ')') {
                throw new IllegalArgumentException("缺少右括号");
            }
            index[0]++;
            return result;
        } else if (c == 'a' || c == 'b' || c == 'c'|| c == 'd') {
            List<Integer> set = getJihe(lists, c);
            index[0]++;
            return new ArrayList<>(set);
        } else {
            throw new IllegalArgumentException("无效操作数: " + c);
        }
    }

    private boolean isOperator(char c) {
        return c == '∪' || c == '∩' || c == '△' || c == '-';
    }

    private List<Integer> applyOperator(List<Integer> left, char op, List<Integer> right) {
        switch (op) {
            case '∪':
                return getBingji(left, right);
            case '∩':
                return getJiaoji(left, right);
            case '-':
                return getChaji(left, right);
            case '△':
                return getBuji(left, right);
            default:
                throw new IllegalArgumentException("未知运算符: " + op);
        }
    }

    private List<Integer> getBingji(List<Integer> a, List<Integer> b) {
        List<Integer> result = new ArrayList<>(a);
        result.addAll(b);
        return result;
    }

    private List<Integer> getJiaoji(List<Integer> a, List<Integer> b) {
        List<Integer> result = new ArrayList<>(a);
        result.retainAll(b);
        return result;
    }

    private List<Integer> getChaji(List<Integer> a, List<Integer> b) {
        List<Integer> result = new ArrayList<>(a);
        result.removeAll(b);
        return result;
    }

    private List<Integer> getBuji(List<Integer> a, List<Integer> b) {
        Set<Integer> tmp = new HashSet<>(a);
        tmp.retainAll(b);
        List<Integer> result = new ArrayList<>();
        result.addAll(a);
        result.addAll(b);
        result.removeAll(tmp);
        return result;
    }

    private List<Integer> getJihe(List<List<Integer>> lists, char c) {
        int idx = -1;
        switch (c) {
            case 'a': idx = 0; break;
            case 'b': idx = 1; break;
            case 'c': idx = 2; break;
            case 'd': idx = 3; break;
            default: throw new IllegalArgumentException("无效集合标识符: " + c);
        }
        List<Integer> set = lists.get(idx);
        if (set == null) {
            throw new IllegalArgumentException("集合" + c + "未提供");
        }
        return new ArrayList<>(set);
    }
}
