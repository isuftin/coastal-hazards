FROM rocker/r-ver:3.3.3

LABEL maintainer="gs-w_eto_eb_federal_employees@usgs.gov"

RUN apt-get update && \
	apt-get install --no-install-recommends -y \
	wget \
	telnet=0.17-36 \
	libcurl3=7.38.0-4+deb8u16 \
	libcurl4-openssl-dev=7.38.0-4+deb8u16 \
	libssl-dev=1.0.1t-1+deb8u12 \
	zlib1g-dev=1:1.2.8.dfsg-2+b1 \
	libssh2-1=1.4.3-4.1+deb8u6 \
	libpng12-dev=1.2.50-2+deb8u3 \
	libxml2=2.9.1+dfsg1-5+deb8u8 \
	libxml2-dev=2.9.1+dfsg1-5+deb8u8 && \
	rm -rf /var/lib/apt/lists/* && \
	apt-get clean

RUN mkdir work

RUN if getent ahosts "sslhelp.doi.net" > /dev/null 2>&1; then \
		wget -O /usr/lib/ssl/certs/DOIRootCA.crt http://sslhelp.doi.net/docs/DOIRootCA2.cer && \
		ln -sf /usr/lib/ssl/certs/DOIRootCA.crt /usr/lib/ssl/certs/`openssl x509 -hash -noout -in /usr/lib/ssl/certs/DOIRootCA.crt`.0; \
	fi

RUN R -e "install.packages('devtools');library(devtools);install.packages(c('Rserve','RCurl','XML'));install_github('USGS-R/hazardItems');"

COPY profile.conf profile.conf
COPY rserve.conf rserve.conf
COPY rserve.pwd rserve.pwd
COPY run.sh run.sh
RUN chmod +x ./run.sh

ENV PORTAL_INTERNAL_HOST=localhost
ENV PORTAL_INTERNAL_HTTPS_PORT=8443

CMD ./run.sh

HEALTHCHECK --interval=2s --timeout=3s \
 CMD sleep 1 | \
 		telnet localhost 6311 | \
		grep -q Rsrv0103QAP1 || exit 1
