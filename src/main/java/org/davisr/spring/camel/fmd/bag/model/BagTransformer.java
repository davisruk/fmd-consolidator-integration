package org.davisr.spring.camel.fmd.bag.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component ("bagTransformer")
public class BagTransformer {

	public static Bag mapBag (ArrayList<Map<String, Object>> maps) {
		Map m = maps.get(0);
		return Bag.builder()
				.id((Integer)m.get("ID"))
				.labelCode((String)m.get("LABEL_CODE"))
				.packs(mapPacks(maps)).build();

	}
	
	public static List<Pack> mapPacks (ArrayList<Map<String, Object>> maps) {
		ArrayList<Pack> al = new ArrayList<Pack>();
		maps.forEach(m -> {
			m.forEach((k, v) -> {
				System.out.println(k + ":" + v);
			});
			try {
				al.add(mapPack(m));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
		return al;
	}
	
	public static Pack mapPack (Map<String, Object> map) throws Exception {
		return Pack.builder()
				.id((Integer)map.get("ID"))
				.gtin((String)map.get("GTIN"))
				.batch((String)map.get("BATCH"))
				.serialNumber((String)map.get("SERIAL_NUMBER"))
				.expiry(new Date(((Timestamp)map.get("EXPIRY_DATE")).getTime()))
				.build();
	}
}
