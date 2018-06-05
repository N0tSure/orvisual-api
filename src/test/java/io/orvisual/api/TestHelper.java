package io.orvisual.api;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import lombok.NonNull;
import org.mockito.ArgumentMatcher;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * <p>
 * Created on 27.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
public final class TestHelper {

    private static final byte[] OKLAHOMA_BYTES = new byte[]{'O', 'K', 'L', 'A', 'H', 'O', 'M', 'A'};

    /**
     * Creates {@link ArgumentMatcher} implementation for test purpose, which check for equality
     * of to {@link Picture} instance, but ignore {@link Picture#getLoadedAt()} field, which might
     * be unpredictable on runtime.
     *
     * @param expectedPicture expected {@link Picture}, must be not {@code null}.
     * @return {@link ArgumentMatcher} instance
     * @see ArgumentMatcher
     */
    public static ArgumentMatcher<Picture> ignoreUnPredictableAttributes(final @NonNull Picture expectedPicture) {
        return argument ->
                expectedPicture.getChecksum().equals(argument.getChecksum()) &&
                expectedPicture.getMimeType().equals(argument.getMimeType());
    }

    /**
     * Creates {@link ArgumentMatcher} implementation for test purpose, which check for equality
     * of to {@link PictureFileItem} instance, but ignore {@link Picture#getLoadedAt()} field,
     * of {@link PictureFileItem#getPictureItem()}, which might be unpredictable on runtime.
     *
     * @param expectedItem expected {@link PictureFileItem} instance, must be not {@code null}.
     * @return {@link ArgumentMatcher} instance
     * @see ArgumentMatcher
     */
    public static ArgumentMatcher<PictureFileItem> ignoreUnPredictableAttributes(final @NonNull PictureFileItem expectedItem) {
        return argument ->
                expectedItem.getPictureItem().getChecksum().equals(argument.getPictureItem().getChecksum()) &&
                expectedItem.getPictureItem().getMimeType().equals(argument.getPictureItem().getMimeType()) &&
                        Arrays.equals(expectedItem.getFileContent(), argument.getFileContent());
    }

    /**
     * Return {@link Supplier} which can used for generation of
     * {@link PictureFileItem} for test purposes.
     *
     * @return {@link PictureFileItem} which never be {@code null}
     */
    public static Supplier<PictureFileItem> uniformPictureItemSupplier() {
        final HashFunction sha256Function = Hashing.sha256();
        return () -> new PictureFileItem(
                new Picture(
                        sha256Function.hashBytes(OKLAHOMA_BYTES).toString(),
                        "foo.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "bar",
                        Instant.EPOCH
                ), OKLAHOMA_BYTES
        );
    }

    public static Supplier<Picture> randomPictureSupplier() {
        final HashFunction sha256Function = Hashing.sha256();
        return () -> {
            String hash = sha256Function.hashBytes(
                    String.valueOf(Math.round(Math.random() * Long.MAX_VALUE)).getBytes()
            ).toString();
            return new Picture(
                    hash,
                    hash + ".jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    hash.substring(0, 4),
                    Instant.now()
            );
        };
    }
}
