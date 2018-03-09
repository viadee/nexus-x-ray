package de.viadee.nexus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

public class ExportServiceTest {

    @Test
    public void exportService() throws IOException {
	ExportService service = new ExportService(false);
	ExportService spyedService = Mockito.spy(service);
	


	RepositoryEntry docker2 = new RepositoryEntry();
	docker2.blobName = "v2/-/blobs/sha256:10172af65f97fa23698349e425caded11ee6dacc0d412ccd800fdf08e5e4630c";
	docker2.contentType = "application/vnd.docker.container.image.v1+json";
	docker2.creationTime = new Date();
	docker2.repoName = "viadee-docker-nas";
	docker2.deleted = false;
	docker2.file = new File("/data/nas/content/vol-40/chap-04/1355be77-8079-4e0f-9247-f8e1e53a957b.properties");
	docker2.size = 5;
	
	RepositoryEntry docker1 = new RepositoryEntry();
	docker1.blobName = "v2/confluence_bpmn-modeler-cloud/manifests/1.1.2";
	docker1.contentType = "application/vnd.docker.image.rootfs.diff.tar.gzip";
	docker1.creationTime = new Date();
	docker1.repoName = "viadee-docker-nas";
	docker1.deleted = false;
	docker1.file = new File("/data/nas/content/vol-21/chap-20/3ee28f98-6cbd-43c4-b278-e414d78621e5.properties");
	docker1.size = 5;
	
	List<RepositoryEntry> entries = new ArrayList<>();
	entries.add(docker1);
	entries.add(docker2);
	Collections.sort(entries);
	
	Mockito.doReturn(entries).when(spyedService).readEntries(Mockito.any());
	
	File myFile =  Files.createTempFile("bla", "x").toFile();
	spyedService.propertiesToCSV(null, myFile);
	
	
	
	
    }
    
}
