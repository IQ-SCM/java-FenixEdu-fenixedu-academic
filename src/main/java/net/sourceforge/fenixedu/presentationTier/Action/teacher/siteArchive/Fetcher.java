/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.fenixedu.presentationTier.Action.teacher.siteArchive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.domain.File;
import net.sourceforge.fenixedu.presentationTier.Action.teacher.siteArchive.streams.FetcherRequestWrapper;
import net.sourceforge.fenixedu.presentationTier.Action.teacher.siteArchive.streams.FetcherServletResponseWrapper;

import org.fenixedu.bennu.io.domain.GenericFile;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;

import com.google.common.io.ByteStreams;

/**
 * The <tt>Fetcher</tt> manages a queue of {@link net.sourceforge.fenixedu.presentationTier.Action.teacher.siteArchive.Resource}
 * and it's responsible for retrieving and transforming each resource in the
 * queue.
 * 
 * <p>
 * Each resource is retrieved by creating a new <tt>RequestDispatcher</tt> to the resource's url and by forwarding the request to
 * that dispatcher. The current request and response are wrapped to avoid unwanted secondary effects and to allow the called to
 * generate it's own content to the user.
 * 
 * <p>
 * If the resource is an HTML page then url's present in the page are transformed using the resource's rules.
 * 
 * @author cfgi
 */
public class Fetcher {

    Queue<Resource> queue;
    Collection<Resource> fetched;

    private Archive archive;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public Fetcher() {
        super();

        this.queue = new LinkedList<Resource>();
        this.fetched = new HashSet<Resource>();
    }

    public Fetcher(Archive archive, HttpServletRequest request, HttpServletResponse response) {
        this();

        this.archive = archive;
        this.request = request;
        this.response = response;
    }

    /**
     * Queues the given resource. The resource is only queued if it isn't
     * already in the and if it wasn't already feched.
     */
    public void queue(Resource resource) {
        if (this.queue.contains(resource) || this.fetched.contains(resource)) {
            return;
        }

        this.queue.add(resource);
    }

    /**
     * If the fetcher has more resources to be fetched.
     * 
     * @return <code>true</code> if {@link #next()} can be called safely and
     *         will return a new Resource
     */
    public boolean hasMore() {
        return this.queue.peek() != null;
    }

    /**
     * Obtains the next Resource in the queue.
     * 
     * @return the next resource in the queue
     * 
     * @exception java.util.NoSuchElementException
     *                if the is no Resource queued
     */
    public Resource next() {
        return this.queue.remove();
    }

    /**
     * Processes the current queue retrieving all resources and saving them in
     * the archive.
     */
    public void process() throws ServletException, IOException {
        while (hasMore()) {
            Resource next = next();

            OutputStream stream = this.archive.getStream(next);
            fetch(next, stream);
        }
    }

    private void markAsFetched(Resource resource) {
        this.fetched.add(resource);
    }

    /**
     * Retrieves the given resource and saves it's contents in the given stream.
     * If the resource is an HTML page then this fetcher ensures that all the
     * urls in the page are transformed, using the resource's rules, and all
     * extra resources generated by the rules are queue.
     * 
     * @param resource
     *            the resource to retrieve
     * @param stream
     *            the destination of the contents
     * 
     * @throws ServletException
     *             if the target resource throws and exception
     * @throws IOException
     *             when a streaming error occurs
     */
    public void fetch(Resource resource, OutputStream stream) throws ServletException, IOException {
        markAsFetched(resource);

        String url = prepareUrl(resource);
//        final int ls = File.ACTION_PATH.lastIndexOf('/');
//        if (url.indexOf("dspace") >= 0) {
//            getDspaceFile(stream, url);
//            return;
//        } else if (url.lastIndexOf(File.ACTION_PATH.substring(ls)) >= 0) {
//            final int lastIndex = url.lastIndexOf(File.ACTION_PATH.substring(ls));
//            final String oid = url.substring(lastIndex + 1);
//            if (StringUtils.isNumeric(oid)) {
//                getLocalFile(stream, oid);
//                return;
//            }
//        }
        if (url.startsWith(File.getFileDownloadPrefix())) {
            writeFileToStream(url, stream);
        } else {
            RequestDispatcher dispatcher = this.request.getRequestDispatcher(url);
            ServletRequest request = createForwardRequest();
            FetcherServletResponseWrapper response = createForwardResponse(resource, stream);
            dispatcher.forward(request, response);
        }
    }

    private void writeFileToStream(String url, OutputStream stream) throws IOException {
        if (url.startsWith(File.getFileDownloadPrefix())) {
            GenericFile fileFromURL = FileDownloadServlet.getFileFromURL(url);
            if (fileFromURL != null) {
                InputStream inputStream = fileFromURL.getStream();
                ByteStreams.copy(inputStream, stream);
                inputStream.close();
                stream.close();
            }

        }
    }

    private String prepareUrl(Resource resource) {
        String url = resource.getUrl();
        String contextPath = this.request.getContextPath();

        if (url.startsWith(contextPath)) {
            url = url.substring(contextPath.length());
        }

        url = url.replaceAll("&amp;", "&");

        return url;
    }

    private ServletRequest createForwardRequest() {
        return new FetcherRequestWrapper(this.request);
    }

    private FetcherServletResponseWrapper createForwardResponse(Resource resource, OutputStream stream) {
        return new FetcherServletResponseWrapper(this.response, this, resource, stream);
    }

}
