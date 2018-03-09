package de.viadee.nexus;

import java.io.File;
import java.util.Date;

public class RepositoryEntry implements Comparable<RepositoryEntry> {

    File file;

    long size;

    String repoName;

    Date creationTime;

    String blobName;

    String contentType;

    boolean deleted;

    @Override
    public String toString() {
	return "RepositoryEntry [blobName=" + blobName + ", repoName=" + repoName + ", size=" + size + ", creationTime="
		+ creationTime + ", contentType=" + contentType + ", deleted=" + deleted + ", file="
		+ file.getAbsolutePath() + "]";
    }

    public String getMavenVersion() {
	if (isMavenArtifact()) {
	    final String[] parts = blobName.split("/");
	    return parts.length > 2 ? parts[parts.length - 2] : null;
	} else {
	    return null;
	}
    }

    public String getMavenArtifactName() {
	if (isMavenArtifact()) {
	    final String[] parts = blobName.split("/");
	    return parts.length > 3 ? parts[parts.length - 3] : null;
	} else {
	    return null;
	}
    }

    public String getMavenGroupName() {
	if (isMavenArtifact()) {
	    final String[] parts = blobName.split("/");
	    final StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < parts.length - 3; i++) {
		if (i > 0) {
		    sb.append('.');
		}
		sb.append(parts[i]);
	    }
	    return sb.toString();
	} else {
	    return null;
	}
    }

    public String getDockerManifestName() {
	if (isDockerArtifact()) {
	    final String[] parts = blobName.split("/");
	    if ("manifests".equals(parts[2])) {
		return parts[1];
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
    }

    public String getDockerManifestVersion() {
	if (isDockerArtifact()) {
	    final String[] parts = blobName.split("/");
	    if ("manifests".equals(parts[2])) {
		return parts[3];
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
    }

    public boolean isMavenArtifact() {
	return !isDockerArtifact();
    }

    public boolean isDockerArtifact() {
	return repoName.contains("docker");
    }

    @Override
    public int compareTo(RepositoryEntry o) {
	if (o == null || o.creationTime == null) {
	    return 1;
	}

	return o.creationTime.before(this.creationTime) ? -1 : 1;
    }
}