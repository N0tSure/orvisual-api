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

    public static ArgumentMatcher<Picture> ignoreUnPredictableAttributes(final @NonNull Picture expectedPicture) {
        return argument ->
                expectedPicture.getChecksum().equals(argument.getChecksum()) &&
                expectedPicture.getMimeType().equals(argument.getMimeType());
    }

    public static ArgumentMatcher<PictureFileItem> ignoreUnPredictableAttributes(final @NonNull PictureFileItem expectedItem) {
        return argument ->
                expectedItem.getPictureItem().getChecksum().equals(argument.getPictureItem().getChecksum()) &&
                expectedItem.getPictureItem().getMimeType().equals(argument.getPictureItem().getMimeType()) &&
                        Arrays.equals(expectedItem.getFileContent(), argument.getFileContent());
    }

    public static Supplier<PictureFileItem> pictureSupplier() {
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
}
