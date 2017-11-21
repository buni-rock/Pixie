package common;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.swing.filechooser.*;

/**
 * A convenience implementation of FileFilter that filters out all files except
 * for those type extensions that it knows about.
 * <p>
 * Extensions are of the type ".foo", which is typically found on Windows and
 * Unix boxes, but not on Macinthosh. Case is ignored.
 * <p>
 * Example - create a new filter that filerts out all files but gif and jpg
 * image files:
 * <p>
 * JFileChooser chooser = new JFileChooser(); LTFileFilter filter = new
 * LTFileFilter( new String{"gif", "jpg"}, "JPEG and GIF Images")
 * chooser.addChoosableFileFilter(filter); chooser.showOpenDialog(this);
 *
 * @author Jeff Dinkins
 * @version 1.9 04/23/99
 */
public final class LTFileFilter extends FileFilter {

    private HashMap<String, LTFileFilter> filters;
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    /**
     * Creates a file filter. If no filters are added, then all files are
     * accepted.
     *
     * @see #addExtension #addExtension
     */
    public LTFileFilter() {
        this.filters = null;
        this.filters = new HashMap<>();
    }

    /**
     * Creates a file filter that accepts files with the given extension.
     * Example: new ExampleFileFilter("jpg");
     *
     * @param extension the extension of the file
     * @see #addExtension #addExtension
     */
    public LTFileFilter(String extension) {
        this(extension, null);
        this.filters = null;
    }

    /**
     * Creates a file filter that accepts the given file type. Example: new
     * ExampleFileFilter("jpg", "JPEG Image Images");
     * <p>
     * Note that the "." before the extension is not needed. If provided, it
     * will be ignored.
     *
     * @param extension   the extension of the file
     * @param description the description of the extension
     * @see #addExtension #addExtension
     */
    public LTFileFilter(String extension, String description) {
        this();
        this.filters = null;
        if (extension != null) {
            addExtension(extension);
        }
        if (description != null) {
            setDescription(description);
        }
    }

    /**
     * Creates a file filter from the given string array. Example: new
     * ExampleFileFilter(String {"gif", "jpg"});
     * <p>
     * Note that the "." before the extension is not needed and will be ignored.
     *
     * @param filters a string array containing the extensions accepted by the filter
     * @see #addExtension #addExtension
     */
    public LTFileFilter(String[] filters) {
        this(filters, null);
        this.filters = null;
    }

    /**
     * Creates a file filter from the given string array and description.
     * Example: new ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG
     * Images");
     * <p>
     * Note that the "." before the extension is not needed and will be ignored.
     *
     * @param filters     the list of accepted extensions
     * @param description the description of the extension list
     * @see #addExtension #addExtension
     */
    public LTFileFilter(String[] filters, String description) {
        this();
        this.filters = null;
        for (String filter : filters) {
            // add filters one by one
            addExtension(filter);
        }
        if (description != null) {
            setDescription(description);
        }
    }

    /**
     * Return true if this file should be shown in the directory pane, false if
     * it shouldn't.
     *
     * Files that begin with "." are ignored.
     *
     * @param f the file being analyzed if it should be shown in the directory
     * pane
     * @return true if this file should be shown in the directory pane, false if
     * it shouldn't.
     * @see #getExtension
     */
    @Override
    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null && filters.get(getExtension(f)) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the extension portion of the file's name .
     *
     * @param f the file for which the file extension should be extracted
     * @return a string representing the file extension
     * @see #getExtension #getExtension
     * @see FileFilter#accept FileFilter#accept
     */
    public String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase(Locale.ENGLISH);
            }
        }
        return null;
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     * <p>
     * For example: the following code will create a filter that filters out all
     * files except those that end in ".jpg" and ".tif":
     * <p>
     * LTFileFilter filter = new LTFileFilter(); filter.addExtension("jpg");
     * filter.addExtension("tif");
     * <p>
     * Note that the "." before the extension is not needed and will be ignored.
     *
     * @param extension the extension which should be added to the list of possible file extensions
     */
    public void addExtension(String extension) {
        if (filters == null) {
            filters = new HashMap<>();
        }
        filters.put(extension.toLowerCase(Locale.ENGLISH), this);
        fullDescription = null;
    }

    /**
     * Returns the human readable description of this filter. For example: "JPEG
     * and GIF Image Files (*.jpg, *.gif)"
     *
     * @return the description of the extensions, in readable form, explanatory
     * for the user
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     * @see FileFilter#getDescription
     */
    @Override
    public String getDescription() {
        if (fullDescription == null) {
            if (description == null || isExtensionListInDescription()) {
                fullDescription = description == null ? "(" : description + " (";
                // build the description from the extension list
                Set<String> extensions = filters.keySet();
                if (extensions != null) {
                    Iterator<String> it = extensions.iterator();
                    fullDescription += "." + it.next();
                    for (it = extensions.iterator(); it.hasNext();) {
                        fullDescription += ", " + it.next();
                    }
                }

////                if (!filters.isEmpty()) {
////                    fullDescription += "." + (String) ;
////                    while (extensions.hasMoreElements()) {
////                        fullDescription += ", " + (String) extensions.nextElement();
////                    }
////                }
//                if (extensions != null) {
//                    fullDescription += "." + (String) extensions.nextElement();
//                    while (extensions.hasMoreElements()) {
//                        fullDescription += ", " + (String) extensions.nextElement();
//                    }
//                }
                fullDescription += ")";
            } else {
                fullDescription = description;
            }
        }
        return fullDescription;
    }

    /**
     * Sets the human readable description of this filter. For example:
     * filter.setDescription("Gif and JPG Images");
     *
     * @param description the description of the extensions, in readable form, explanatory for the user
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     */
    public void setDescription(String description) {
        this.description = description;
        fullDescription = null;
    }

    /**
     * Determines whether the extension list (.jpg, .gif, etc) should show up in
     * the human readable description.
     * <p>
     * Only relevant if a description was provided in the constructor or using
     * setDescription();
     *
     * @param showExtensions true if the list of extensions should show in the description; false otherwise
     * @see getDescription
     * @see setDescription
     * @see isExtensionListInDescription
     */
    public void setExtensionListInDescription(boolean showExtensions) {
        useExtensionsInDescription = showExtensions;
        fullDescription = null;
    }

    /**
     * Returns whether the extension list (.jpg, .gif, etc) should show up in
     * the human readable description.
     * <p>
     * Only relevant if a description was provided in the constructor or using
     * setDescription();
     *
     * @return true if the list of extensions should show in the description; false otherwise
     * @see getDescription
     * @see setDescription
     * @see setExtensionListInDescription
     */
    public boolean isExtensionListInDescription() {
        return useExtensionsInDescription;
    }
}
