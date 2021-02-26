package org.generictech.accounts.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * Class to handle errors with RestTemplate requests.
 * @author Jaden Wilson
 * @since 1.0
 */
public class RestTemplateErrorHandler implements ResponseErrorHandler{

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR 
				|| response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
			throw new ProcessingRuntimeException("Unable to process request");
		} else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
			if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new UserNotFoundException();
			} else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
				throw new UserNotFoundException("Unable to validate user");
			}
		}
		
	}

}
