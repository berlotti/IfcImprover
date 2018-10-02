package org.opensourcebim.modelsetanalyzer;

import java.nio.file.Paths;
import java.util.List;

import org.bimserver.client.json.JsonBimServerClientFactory;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SProjectSmall;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.shared.BimServerClientFactory;
import org.bimserver.shared.ChannelConnectionException;
import org.bimserver.shared.UsernamePasswordAuthenticationInfo;
import org.bimserver.shared.exceptions.BimServerClientException;
import org.bimserver.shared.exceptions.ServiceException;

public class PontsteigerExport {
	public static void main(String[] args) {
		try (BimServerClientFactory factory = new JsonBimServerClientFactory("https://epic.logic-labs.nl")) {
			try (BimServerClientInterface client = factory.create(new UsernamePasswordAuthenticationInfo("admin@bimserver.org", "admin"))) {
				ModelSetAnalyzer modelSetAnalyzer = new ModelSetAnalyzer(client);
				List<SProject> projects = client.getServiceInterface().getProjectsByName("Pontsteiger Full 2");
				SProject mainProject = projects.get(0);
				for (SProjectSmall smallProject : client.getServiceInterface().getAllRelatedProjects(mainProject.getOid())) {
					long roid = smallProject.getLastRevisionId();
					if (roid != -1) {
						SProject project = client.getServiceInterface().getProjectByPoid(smallProject.getOid());
						if (project.getSubProjects().isEmpty()) {
							modelSetAnalyzer.addRevision(project, roid);
						}
					}
				}
				modelSetAnalyzer.awaitTermination();
				modelSetAnalyzer.getResults().toExcel(Paths.get("pontsteiger.xls"));
			}
		} catch (BimServerClientException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (ChannelConnectionException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
