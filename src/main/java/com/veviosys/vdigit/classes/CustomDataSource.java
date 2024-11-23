package com.veviosys.vdigit.classes;

import java.io.*;
import javax.activation.*;
import javax.mail.internet.*;
import javax.mail.util.SharedByteArrayInputStream;
public class CustomDataSource  implements DataSource {
    private byte[] data;	// data
    private int len = -1;
    private String type;	// content-type
    private String name = "";

    static class DSByteArrayOutputStream extends ByteArrayOutputStream {
	public byte[] getBuf() {
	    return buf;
	}

	public int getCount() {
	    return count;
	}
    }

    /**
     * Create a CustomDataSource with data from the
     * specified InputStream and with the specified MIME type.
     * The InputStream is read completely and the data is
     * stored in a byte array.
     *
     * @param	is	the InputStream
     * @param	type	the MIME type
     * @exception	IOException	errors reading the stream
     */
    public CustomDataSource(InputStream is, String type) throws IOException {
	DSByteArrayOutputStream os = new DSByteArrayOutputStream();
	byte[] buf = new byte[8192];
	int len;
	while ((len = is.read(buf)) > 0)
	    os.write(buf, 0, len);
	this.data = os.getBuf();
	this.len = os.getCount();

	/*
	 * ByteArrayOutputStream doubles the size of the buffer every time
	 * it needs to expand, which can waste a lot of memory in the worst
	 * case with large buffers.  Check how much is wasted here and if
	 * it's too much, copy the data into a new buffer and allow the
	 * old buffer to be garbage collected.
	 */
	if (this.data.length - this.len > 256*1024) {
	    this.data = os.toByteArray();
	    this.len = this.data.length;	// should be the same
	}
        this.type = type;
    }

    /**
     * Create a CustomDataSource with data from the
     * specified byte array and with the specified MIME type.
     *
     * @param	data	the data
     * @param	type	the MIME type
     */
    public CustomDataSource(byte[] data, String type) {
        this.data = data;
	this.type = type;
    }

    /**
     * Create a CustomDataSource with data from the
     * specified String and with the specified MIME type.
     * The MIME type should include a <code>charset</code>
     * parameter specifying the charset to be used for the
     * string.  If the parameter is not included, the
     * default charset is used.
     *
     * @param	data	the String
     * @param	type	the MIME type
     * @exception	IOException	errors reading the String
     */
    public CustomDataSource(String data, String type) throws IOException {
	String charset = null;
	try {
	    ContentType ct = new ContentType(type);
	    charset = ct.getParameter("charset");
        //System.out.println("--------charset "+charset);
	} catch (ParseException pex) {
	    // ignore parse error
	}
	charset = MimeUtility.javaCharset(charset);
	if (charset == null)
	    charset = MimeUtility.getDefaultJavaCharset();
	// XXX - could convert to bytes on demand rather than copying here
	this.data = data.getBytes(charset);
	this.type = type;
    }

    /**
     * Return an InputStream for the data.
     * Note that a new stream is returned each time
     * this method is called.
     *
     * @return		the InputStream
     * @exception	IOException	if no data has been set
     */
    @Override
    public InputStream getInputStream() throws IOException {
	if (data == null)
	    throw new IOException("no data");
	if (len < 0)
	    len = data.length;
	return new SharedByteArrayInputStream(data, 0, len);
    }

    /**
     * Return an OutputStream for the data.
     * Writing the data is not supported; an <code>IOException</code>
     * is always thrown.
     *
     * @exception	IOException	always
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
	throw new IOException("cannot do this");
    }

    /**
     * Get the MIME content type of the data.
     *
     * @return	the MIME type
     */
    @Override
    public String getContentType() {
        return type;
    }

    /**
     * Get the name of the data.
     * By default, an empty string ("") is returned.
     *
     * @return	the name of this data
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the name of the data.
     *
     * @param	name	the name of this data
     */
    public void setName(String name) {
	this.name = name;
    }
}
