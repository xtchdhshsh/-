package comlisanshuxue.lisan.MingTi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@Api(tags = "范式")
public class experiment3 {

//	public static void main(String[] args) {
//		/*输入命题公式(p→﹁q)→r
//		 *输出主析取范式、主合取范式
//		 *输出成真赋值、成假赋值
//		 * 非为‘﹁’，析取为‘∨’，合取为‘∧’蕴涵为‘→’，等价为‘↔’*/
//		String form;
//		Scanner scanf = new Scanner(System.in);
//		System.out.println("请输入命题公式:(非为‘﹁’，析取为‘∨’，合取为‘∧’蕴涵为‘→’，等价为‘↔’)");
//		form = scanf.nextLine();
//		NormalForm normal = new NormalForm(form);
//		normal.calculate();
//		normal.pdnf();
//
//		normal.pcnf();
//		normal.comeTrue();
//		normal.aFalse();
//	}
	@ApiOperation("主析取主合取范式和成真成假赋值")
	@GetMapping("/fanshi/{fan}")
	public String[] fanshi(@PathVariable String fan) {
		/*输入命题公式(p→﹁q)→r
		 *输出主析取范式、主合取范式
		 *输出成真赋值、成假赋值
		 * 非为‘﹁’，析取为‘∨’，合取为‘∧’蕴涵为‘→’，等价为‘↔’*/
		String form;
		String[] jieguo = new String[4];
//		Scanner scanf = new Scanner(System.in);
//		System.out.println("请输入命题公式:(非为‘﹁’，析取为‘∨’，合取为‘∧’蕴涵为‘→’，等价为‘↔’)");
//		form = scanf.nextLine();
		NormalForm normal = new NormalForm(fan);
		normal.calculate();
		String zhuxi = normal.pdnf();

		String zhuhe =  normal.pcnf();

		String chengzhen =  normal.comeTrue();

		String chengjia = normal.aFalse();
		jieguo[0]=zhuxi;
		jieguo[1] = zhuhe;
		jieguo[2] = chengzhen;
		jieguo[3] = chengjia;

		return jieguo;

	}

}