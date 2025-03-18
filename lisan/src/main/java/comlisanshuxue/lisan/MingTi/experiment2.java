package comlisanshuxue.lisan.MingTi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.*;
/*2.对给出的任意一个命题公式（不超过四个命题变元），
 * 画出其真值表。
 * （1）﹁(﹁p∨q)∧q的真值表；
 * （2）(p∨q)→﹁r的真值表；
 */
//public class experiment2 {
//	public static void main(String[] args) {
//
//		Scanner scanf=new Scanner(System.in);
//		int[] w=new int[4];
//		System.out.println("输入命题公式（不超过四个命题变元）：");
//		System.out.println("非为‘﹁’，析取为‘∨’，合取为‘∧’蕴涵为‘→’，等价为‘↔’");
//		String prf = scanf.nextLine();
//		NormalForm form = new NormalForm(prf);
//		form.calculate();
//		String shuchu = form.table();
//
//		System.out.println(shuchu);
//	}
//}

@Api(tags = "真值表")
@RestController
public class experiment2 {
//	public static void main(String[] args) {
//		String a = zhenzhi("(p∨q)→﹁r");
//	}
	@ApiOperation("打印真值表")
	@GetMapping("/zhenzhi/{zhenzhi}")
	public Res zhenzhi(@PathVariable String zhenzhi) {

		//Scanner scanf=new Scanner(System.in);
//		System.out.println("输入命题公式（不超过四个命题变元）：");
//		System.out.println("非为‘﹁’，析取为‘∨’，合取为‘∧’蕴涵为‘→’，等价为‘↔’");
//		System.out.println(zhenzhi);
		//String prf = scanf.nextLine();
		NormalForm form = new NormalForm(zhenzhi);
		form.calculate();
		int [][]data=form.table();
		ArrayList<String> va=form.GetValName();
		va.set(va.size()-1,zhenzhi);
		Res re=new Res(data, va);
		return re;
	}
}