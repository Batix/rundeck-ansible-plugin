# Ubuntu 16.04 based, runs as rundeck user
# https://hub.docker.com/r/rundeck/rundeck/tags
FROM rundeck/rundeck:3.0.17

MAINTAINER David Kirstein <dak@batix.com>

ENV ANSIBLE_HOST_KEY_CHECKING=false
ENV RDECK_BASE=/home/rundeck
ENV MANPATH=${MANPATH}:${RDECK_BASE}/docs/man
ENV PATH=${PATH}:${RDECK_BASE}/tools/bin
ENV PROJECT_BASE=${RDECK_BASE}/projects/Test-Project

#  mkdir -p /etc/ansible \
#  ${PROJECT_BASE}/acls \

# install ansible
# base image already installed: curl, openjdk-8-jdk-headless, ssh-client, sudo, uuid-runtime, wget
# (see https://github.com/rundeck/rundeck/blob/master/docker/ubuntu-base/Dockerfile)
RUN sudo apt-get -y update \
  && sudo apt-get -y --no-install-recommends install ca-certificates python3-pip python3-setuptools \
    python3-venv sshpass zip unzip \
  # https://pypi.org/project/ansible/#history
  && sudo -H pip3 install --upgrade pip==20.3.4 \
  && sudo -H pip3 --no-cache-dir install ansible==2.9.14 \
  && sudo rm -rf /var/lib/apt/lists/* \
  && mkdir -p ${PROJECT_BASE}/etc/ \
  && sudo mkdir /etc/ansible

# install ansible 2.10 in a virtualenv
RUN mkdir -p $HOME/.venv \
  && python3 -m venv $HOME/.venv/ansible-2.10 \
  && source $HOME/.venv/ansible-2.10/bin/activate \
  && pip install --upgrade pip==20.3.4 \
  && pip install ansible==2.10.1

# add default project
COPY --chown=rundeck:rundeck docker/project.properties ${PROJECT_BASE}/etc/

# add SSH key
COPY --chown=rundeck:rundeck docker/ssh-key ${RDECK_BASE}/.ssh/ssh-node.key

# remove embedded rundeck-ansible-plugin
RUN zip -d rundeck.war WEB-INF/rundeck/plugins/rundeck-ansible-plugin-* \
  && unzip -C rundeck.war WEB-INF/rundeck/plugins/manifest.properties \
  && sed -i "s/\(.*\)\(rundeck-ansible-plugin-.*\.jar,\)\(.*\)/\1\3/" WEB-INF/rundeck/plugins/manifest.properties \
  && zip -u rundeck.war WEB-INF/rundeck/plugins/manifest.properties \
  && rm WEB-INF/rundeck/plugins/manifest.properties

# add locally built ansible plugin
COPY --chown=rundeck:rundeck build/libs/ansible-plugin-*.jar ${RDECK_BASE}/libext/
