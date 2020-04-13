package org.eclipse.actf.examples.htmlchecker;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

import static org.junit.Assert.*;

public class HtmlCheckerTest {

    @Before
    public void setUp() throws Exception {
        BundleContext context = new BundleContext() {
            @Override
            public String getProperty(String key) {
                throw new NotImplementedException();
            }

            @Override
            public Bundle getBundle() {
                throw new NotImplementedException();
            }

            @Override
            public Bundle installBundle(String location, InputStream input) throws BundleException {
                throw new NotImplementedException();
            }

            @Override
            public Bundle installBundle(String location) throws BundleException {
                throw new NotImplementedException();
            }

            @Override
            public Bundle getBundle(long id) {
                throw new NotImplementedException();
            }

            @Override
            public Bundle[] getBundles() {
                throw new NotImplementedException();
            }

            @Override
            public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
                throw new NotImplementedException();
            }

            @Override
            public void addServiceListener(ServiceListener listener) {
                throw new NotImplementedException();
            }

            @Override
            public void removeServiceListener(ServiceListener listener) {
                throw new NotImplementedException();
            }

            @Override
            public void addBundleListener(BundleListener listener) {
                throw new NotImplementedException();
            }

            @Override
            public void removeBundleListener(BundleListener listener) {
                throw new NotImplementedException();
            }

            @Override
            public void addFrameworkListener(FrameworkListener listener) {
                throw new NotImplementedException();
            }

            @Override
            public void removeFrameworkListener(FrameworkListener listener) {
                throw new NotImplementedException();
            }

            @Override
            public ServiceRegistration<?> registerService(String[] clazzes, Object service, Dictionary<String, ?> properties) {
                throw new NotImplementedException();
            }

            @Override
            public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties) {
                throw new NotImplementedException();
            }

            @Override
            public <S> ServiceRegistration<S> registerService(Class<S> clazz, S service, Dictionary<String, ?> properties) {
                throw new NotImplementedException();
            }

            @Override
            public <S> ServiceRegistration<S> registerService(Class<S> clazz, ServiceFactory<S> factory, Dictionary<String, ?> properties) {
                throw new NotImplementedException();
            }

            @Override
            public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                throw new NotImplementedException();
            }

            @Override
            public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
                throw new NotImplementedException();
            }

            @Override
            public ServiceReference<?> getServiceReference(String clazz) {
                throw new NotImplementedException();
            }

            @Override
            public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
                throw new NotImplementedException();
            }

            @Override
            public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter) throws InvalidSyntaxException {
                throw new NotImplementedException();
            }

            @Override
            public <S> S getService(ServiceReference<S> reference) {
                throw new NotImplementedException();
            }

            @Override
            public boolean ungetService(ServiceReference<?> reference) {
                throw new NotImplementedException();
            }

            @Override
            public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference) {
                throw new NotImplementedException();
            }

            @Override
            public File getDataFile(String filename) {
                throw new NotImplementedException();
            }

            @Override
            public Filter createFilter(String filter) throws InvalidSyntaxException {
                throw new NotImplementedException();
            }

            @Override
            public Bundle getBundle(String location) {
                throw new NotImplementedException();
            }
        };
        InternalPlatform.getDefault().start(context);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() throws Exception {
        HtmlChecker checker = new HtmlChecker();
        IApplicationContext context = new IApplicationContext() {
            @Override
            public Map getArguments() {
                return null;
            }

            @Override
            public void applicationRunning() {

            }

            @Override
            public String getBrandingApplication() {
                return null;
            }

            @Override
            public String getBrandingName() {
                return null;
            }

            @Override
            public String getBrandingDescription() {
                return null;
            }

            @Override
            public String getBrandingId() {
                return null;
            }

            @Override
            public String getBrandingProperty(String key) {
                return null;
            }

            @Override
            public Bundle getBrandingBundle() {
                return null;
            }

            @Override
            public void setResult(Object result, IApplication application) {

            }
        };

        checker.start(context);
    }
}
