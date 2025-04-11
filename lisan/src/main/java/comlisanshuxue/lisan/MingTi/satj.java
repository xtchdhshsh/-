package comlisanshuxue.lisan.MingTi;
import comlisanshuxue.lisan.MingTi.NormalForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(tags = "SAT")
public class satj {

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

	@ApiOperation("判断可满足式")
	@GetMapping("/sat/{fan}")
	public String[] fanshi(@PathVariable String fan) {

		/*输入命题公式(p→﹁q)→r
		 *输出主析取范式、主合取范式
		 *输出成真赋值、成假赋值
		 * 非为‘﹁’，析取为‘∨’，合取为‘∧’蕴涵为‘→’，等价为‘↔’*/
		String form;
		String[] jieguo = new String[4];
		String[] re = new String[2];
//		Scanner scanf = new Scanner(System.in);
//		System.out.println("请输入命题公式:(非为‘﹁’，析取为‘∨’，合取为‘∧’蕴涵为‘→’，等价为‘↔’)");
//		form = scanf.nextLine();
		NormalForm normal = new NormalForm(fan);
		normal.calculate();
//		String zhuxi = normal.pdnf();
//		System.out.println("主析取范式为：：："+zhuxi);
//		String zhuhe =  normal.pcnf();
//		System.out.println("主合取范式为：：："+zhuhe);
		String chengzhen =  normal.comeTrue();

//		String chengjia = normal.aFalse();
//		jieguo[0]=zhuxi;
//		jieguo[1] = zhuhe;
		jieguo[2] = chengzhen;
//		jieguo[3] = chengjia;
		if(re[1]!=null){
			re[0] = "true";
			re[1] = chengzhen;
			return re;
		}
		re[0] = "false";
		return re;
		//永假式例子：p∧r∧﹁(q→p)
		//(p∧q)∨r可满足式子 13567
		//(p∨﹁q∨r)∧(﹁p∨﹁q∨r)∧(﹁p∨q∨﹁r)∧(﹁p∨q∨r) 0137
	}

}