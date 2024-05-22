package com.bh.cp.dashboard.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.bh.cp.dashboard.entity.CommonBlobs;
import com.bh.cp.dashboard.repository.CommonBlobsRepository;

@Component
public class StoreImage implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(StoreImage.class);

	private CommonBlobsRepository commonBlobsRepository;

	public StoreImage(@Autowired CommonBlobsRepository blobsRepository) {
		super();
		this.commonBlobsRepository = blobsRepository;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Map<Integer, String> map = new LinkedHashMap<>();

		// Static images
		map.put(1, "static-images\\Technical Enhancement & Notifications.svg");
		map.put(2, "static-images\\Training.svg");
		map.put(3, "static-images\\Maintenance optimizer tasks timeline.svg");
		map.put(4, "static-images\\Events timeline.svg");
		map.put(5, "static-images\\RM&D cases in open status.svg");
		map.put(6, "static-images\\RM&D open cases per criticality.svg");
		map.put(7, "static-images\\RM&D cases.svg");
		map.put(8, "static-images\\Last RM&D case.svg");
		map.put(9, "static-images\\Trip Reduction Program.svg");
		map.put(10, "static-images\\GT AxCo WW optimization.svg");
		map.put(11, "static-images\\Filter change advisory.svg");
		map.put(12, "static-images\\Centrifugal Compressor Operating Point.svg");
		map.put(13, "static-images\\Centrifugal Compressor operating profile.svg");
		map.put(14, "static-images\\Centrifugal Compressor map.svg");
		map.put(15, "static-images\\4B thrust bearing load.svg");
		map.put(16, "static-images\\7B thrust bearing load.svg");
		map.put(17, "static-images\\4B thrust bearing load summary.svg");
		map.put(18, "static-images\\7B thrust bearing load summary.svg");
		map.put(19, "static-images\\Spinning Reserve.svg");
		map.put(20, "static-images\\Spinning Reserve summary.svg");
		map.put(21, "static-images\\Carbon Optimizer.svg");
		map.put(22, "static-images\\GT DLE health status.svg");
		map.put(23, "static-images\\Health status.svg");
		map.put(24, "static-images\\Health Index.svg");
		map.put(25, "static-images\\Operational & Maintenance Manuals.svg");
		map.put(26, "static-images\\Running Hours.svg");
		map.put(27, "static-images\\Total Starts.svg");
		map.put(28, "static-images\\Total Trip.svg");
		map.put(29, "static-images\\Max Availability.svg");
		map.put(30, "static-images\\SR.svg");
		map.put(31, "static-images\\MTBT.svg");
		map.put(32, "static-images\\Open Cases.svg");
		map.put(33, "static-images\\Closed Cases.svg");
		map.put(34, "static-images\\1B thrust bearing load.svg");
		map.put(35, "static-images\\1B thrust bearing load summary.svg");
		map.put(36, "static-images\\Max Reliability.svg");
		map.put(37, "static-images\\GT AxCo WW optimization summary.svg");
		map.put(38, "static-images\\Carbon Optimizer-1.svg");

		// Grey images
		map.put(39, "grey-images\\Technical Enhancement & Notifications.svg");
		map.put(40, "grey-images\\Training.svg");
		map.put(41, "grey-images\\Maintenance optimizer tasks timeline.svg");
		map.put(42, "grey-images\\Events timeline.svg");
		map.put(43, "grey-images\\RM&D cases in open status.svg");
		map.put(44, "grey-images\\RM&D open cases per criticality.svg");
		map.put(45, "grey-images\\RM&D cases.svg");
		map.put(46, "grey-images\\Last RM&D case.svg");
		map.put(47, "grey-images\\Trip Reduction Program.svg");
		map.put(48, "grey-images\\GT AxCo WW optimization.svg");
		map.put(49, "grey-images\\Filter change advisory.svg");
		map.put(50, "grey-images\\Centrifugal Compressor Operating Point.svg");
		map.put(51, "grey-images\\Centrifugal Compressor operating profile.svg");
		map.put(52, "grey-images\\Centrifugal Compressor map.svg");
		map.put(53, "grey-images\\4B thrust bearing load.svg");
		map.put(54, "grey-images\\7B thrust bearing load.svg");
		map.put(55, "grey-images\\4B thrust bearing load summary.svg");
		map.put(56, "grey-images\\7B thrust bearing load summary.svg");
		map.put(57, "grey-images\\Spinning Reserve.svg");
		map.put(58, "grey-images\\Spinning Reserve summary.svg");
		map.put(59, "grey-images\\Carbon Optimizer.svg");
		map.put(60, "grey-images\\GT DLE health status.svg");
		map.put(61, "grey-images\\Health status.svg");
		map.put(62, "grey-images\\Health Index.svg");
		map.put(63, "grey-images\\Operational & Maintenance Manuals.svg");
		map.put(64, "grey-images\\Running Hours.svg");
		map.put(65, "grey-images\\Total Starts.svg");
		map.put(66, "grey-images\\Total Trip.svg");
		map.put(67, "grey-images\\Max Availability.svg");
		map.put(68, "grey-images\\SR.svg");
		map.put(69, "grey-images\\MTBT.svg");
		map.put(70, "grey-images\\Open Cases.svg");
		map.put(71, "grey-images\\Closed Cases.svg");
		map.put(72, "grey-images\\1B thrust bearing load.svg");
		map.put(73, "grey-images\\1B thrust bearing load summary.svg");
		map.put(74, "grey-images\\Max Reliability.svg");
		map.put(75, "grey-images\\GT AxCo WW optimization summary.svg");
		map.put(76, "grey-images\\Carbon Optimizer-1.svg");

		map.put(78, "static-images\\Max Availability-1.svg");
		map.put(79, "grey-images\\Max Availability-1.svg");
		
		map.put(80, "static-images\\Max Reliability-1.svg");
		map.put(81, "grey-images\\Max Reliability-1.svg");	

		// Company logo
		map.put(77, "BH logo.png");

		// KPIs icons
		map.put(101, "icons\\closed-cases-icon.png");
		map.put(102, "icons\\fired-hours-icon.png");
		map.put(103, "icons\\mtbt-icon.png");
		map.put(104, "icons\\open-cases-icon.png");
		map.put(105, "icons\\sr-icon.png");
		map.put(106, "icons\\total-start-icon.png");
		map.put(107, "icons\\total-trip-icon.png");

		// Widgets icons
		map.put(151, "icons\\technical-icon.svg");
		map.put(152, "icons\\training-icon.svg");
		map.put(153, "icons\\omm-icon.svg");
		map.put(154, "icons\\decrement-icon.png");
		map.put(155, "icons\\increment-icon.png");
		map.put(156, "icons\\running-icon.png");
		map.put(157, "icons\\stopped-icon.png");
		map.put(158, "icons\\purple-star-icon.png");

		// Info images
		map.put(201, "info\\Rmd-Criticality-info.png");

		List<Integer> map2 = map.entrySet().stream().map(Entry::getKey).toList();
		map2.stream().forEach(i -> {
			Optional<CommonBlobs> findByMaterial = commonBlobsRepository.findById(i);
			if (findByMaterial.isPresent()) {
				CommonBlobs commonBlobs = findByMaterial.get();
				if (commonBlobs.getMaterial() == null) {
					ClassPathResource resource = new ClassPathResource("images\\" + map.get(i));
					byte[] readFileToByteArray = null;
					try {
						readFileToByteArray = resource.getContentAsByteArray();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
					commonBlobs.setMaterial(readFileToByteArray);
					commonBlobsRepository.save(commonBlobs);
				}
			}
		});

	}

}
