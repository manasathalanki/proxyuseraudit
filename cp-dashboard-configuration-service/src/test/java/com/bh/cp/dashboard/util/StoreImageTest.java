package com.bh.cp.dashboard.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.ApplicationArguments;

import com.bh.cp.dashboard.entity.CommonBlobs;
import com.bh.cp.dashboard.repository.CommonBlobsRepository;

class StoreImageTest {

	@InjectMocks
	private StoreImage storeImage;

	@Mock
	private CommonBlobsRepository commonBlobsRepository;

	private CommonBlobs commonBlobs;

	Map<Integer, String> map;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		commonBlobs = new CommonBlobs();
		commonBlobs.setExtension("png");
		commonBlobs.setId(1);
		map = new HashMap<>();
		map.put(1, "logo.png");
	}

	@Test
	void test() throws Exception {
      when(commonBlobsRepository.findById(1)).thenReturn(Optional.of(commonBlobs));
      commonBlobsRepository.save(commonBlobs);
      ApplicationArguments args=mock(ApplicationArguments.class);
      storeImage.run(args);
       assertNotNull(commonBlobs);

	}

}
