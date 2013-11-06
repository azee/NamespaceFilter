package com.mycompany.namespace;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.apache.http.client.utils.URIBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: azee
 */
public class UsageExample {
    private static final Logger log = LogManager.getLogger(UsageExample.class);

    public BeanExample getExampleBean() throws Exception {
        BeanExample beanExample;
        try {

            URIBuilder builder = new URIBuilder("http://some.service.com/url");

            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("SomeParamName", "SomeParamValue");

            HttpPost post = new HttpPost(builder.build());
            post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            // Send data
            HttpResponse response = client.execute(post);

            JAXBContext contextObj = JAXBContext.newInstance(BeanExample.class);
            Unmarshaller unmarshallerObj = contextObj.createUnmarshaller();

            //Response doesn't have namespaces, it can't be unmarshalled directly - adding namespaces
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            //Create an XMLReader to use with our filter
            XMLReader reader = XMLReaderFactory.createXMLReader();

            //Create the filter (to add namespace) and set the xmlReader as its parent.
            NamespaceFilter inFilter = new NamespaceFilter("urn:namespace.mycompany.com", true);
            inFilter.setParent(reader);

            //Prepare the input
            InputSource is = new InputSource(rd);

            //Create a SAXSource specifying the filter
            SAXSource source = new SAXSource(inFilter, is);

            //Do unmarshalling
            beanExample = (BeanExample)unmarshallerObj.unmarshal(source);

            rd.close();
        }
        catch (Exception e) {
            log.error("Error occur while connecting to blackbox", e);
            throw e;
        }
        return beanExample;
    }
}
