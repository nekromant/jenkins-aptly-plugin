/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ncrmnt.aptly;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Run;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.tasks.BuildStepDescriptor;
import hudson.util.FormValidation;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author necromant
 */
public class PackageAdder extends Builder {

    private final String name;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public PackageAdder(String name) {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
    
    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.
        List<Run.Artifact> art = build.getArtifacts();
        AptlyConnector aptly = AptlyConnector.getInstance();
        String reponame = this.name;
        
        try {
        EnvVars env = build.getEnvironment(listener);
        reponame = env.expand(this.name);
        } catch (Exception e) { };
        
        for (Run.Artifact a : art) {
            String path = a.getFile().getAbsolutePath();
            String ext = FilenameUtils.getExtension(path);
            if (ext.equals("deb")) {
                listener.getLogger().println("APTLY: Adding " + a.getFileName() + " to repo " + reponame + " !");
                int ret = aptly.addPackage(listener.getLogger(), reponame, path);
                if (ret != 0)
                        return false;
            } else {
                listener.getLogger().println("APTLY: Skipping artifact " + path + ", not a deb file!");                
            }
        }
        return true;
    }
    
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        private static String repo;
        
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "APTLY: Add artifacts to repo";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            repo = formData.getString("name");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req, formData);
        }

        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            AptlyConnector a = AptlyConnector.getInstance();
            List<String> repolist = a.getRepoList();
            if (!repolist.contains(value))
                return FormValidation.error("aptly doesn't have repository " + value + ", available: " + repolist.toString());
            return FormValidation.ok();
        }

    }
}
