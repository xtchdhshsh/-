package comlisanshuxue.lisan.MingTi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Api(tags = "真值表")
@RestController
public class experiment2 {
	@ApiOperation("打印真值表")
	@GetMapping("/zhenzhi/{zhenzhi}")
	public Res zhenzhi(@PathVariable String zhenzhi) {
		//非为‘﹁’，析取为‘∨’，合取为‘∧’蕴涵为‘→’，等价为‘↔’
		NormalForm form = new NormalForm(zhenzhi);
		form.calculate();
		int [][]data=form.table();
		ArrayList<String> va=form.GetValName();
		va.set(va.size()-1,zhenzhi);
        return new Res(data, va);
	}
}