import java.util.Arrays;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.core.AVOSCloud;
import cn.leancloud.query.AVCloudQueryResult;
import io.reactivex.Observable;

/**
 * @author kunlingou
 * @date 2019/10/20
 */
public class LeanCloudTest {
	
	public static void main(String[] args) {
		AVOSCloud.initialize("EVsneirELmgOUBYlT40edXpt-gzGzoHsz", "n16mFPPYxAOdKRoIPef3tUSV");
//		AVObject avObject = new AVObject("Test");
//		avObject.put("name", "goukunlin");
//		avObject.saveInBackground().blockingSubscribe();
		Observable<AVCloudQueryResult> doCloudQueryInBackground = AVQuery.doCloudQueryInBackground("select * from Counter");
		AVCloudQueryResult blockingFirst = doCloudQueryInBackground.blockingFirst();
		List<AVObject> find = (List<AVObject>) blockingFirst.getResults();
//		AVQuery<AVObject> query = AVQuery.getQuery("Counter");
//		List<AVObject> find = query.find();
		System.out.println(Arrays.deepToString(find.toArray()));
	}
}
