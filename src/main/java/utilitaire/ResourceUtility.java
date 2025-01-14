package utilitaire;

import javax.imageio.ImageIO;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;

import static java.awt.Font.DIALOG;
import static java.awt.Font.PLAIN;
import static java.awt.Font.TRUETYPE_FONT;
import static java.awt.Font.createFont;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * resource utility methods
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.0
 */
public class ResourceUtility {

	private static final ClassLoader CLASS_LOADER = ResourceUtility.class.getClassLoader();
	private static final Font DEFAULT_FONT = new Font(DIALOG, PLAIN, 12);

	/**
	 * Opens an input stream on a named resource.
	 *
	 * @param resourceName name of the resource to open
	 * @return open input stream, {@code null} if the resource is not found
	 * @since 0.0.0
	 */
	public static InputStream openInputStream(String resourceName) {
		return CLASS_LOADER.getResourceAsStream(resourceName);
	}

	/**
	 * Gets a URL to a named resource.
	 *
	 * @param resourceName name of the resource
	 * @return URL to the resource
	 */
	public static URL getResourceUrl(String resourceName) {

		var url = CLASS_LOADER.getResource(resourceName);
		return requireNonNull(url);
	}

	/**
	 * Loads a font resource.
	 *
	 * @param resourceName name of the font resource
	 * @return loaded font
	 * @since 0.0.0
	 */
	public static Font loadFont(String resourceName) {

		var font = DEFAULT_FONT;

		try (var inputStream = openInputStream(resourceName)) {
			font = createFont(TRUETYPE_FONT, requireNonNull(inputStream));
		} catch (IOException | FontFormatException | NullPointerException exception) {
			throw new RuntimeException("error while loading font: " + resourceName, exception);
		}

		return font;
	}

	/**
	 * Reads all lines from a resource.
	 *
	 * @param resourceName name of the resource to read
	 * @param charset charset to use for character decoding
	 * @return read lines
	 * @since 0.0.0
	 */
	public static String readText(String resourceName, Charset charset) {

		try (
				var inputStream = CLASS_LOADER.getResourceAsStream(resourceName);
				var reader = new InputStreamReader(requireNonNull(inputStream), charset);
				var bufferedReader = new BufferedReader(reader)) {

			return bufferedReader.lines().collect(joining("\n"));

		} catch (IOException cause) {

			throw new RuntimeException("error while reading lines of resource " + resourceName, cause);
		}
	}

	/**
	 * Loads an image resource.
	 *
	 * @param resourceName name of the image resource to open
	 * @return loaded image resource
	 * @since 0.0.0
	 */
	public static Optional<BufferedImage> loadOptionalImage(String resourceName) {

		Optional<BufferedImage> image;

		var url = CLASS_LOADER.getResource(resourceName);

		if (url == null) {

			image = Optional.empty();

		} else {

			try {
				image = Optional.of(ImageIO.read(url));
			} catch (IOException ioException) {
				image = Optional.empty();
			}
		}

		return image;
	}

	/**
	 * private constructor to prevent instantiation of this utility class
	 *
	 * @since 0.0.0
	 */
	private ResourceUtility() {

	}
}
