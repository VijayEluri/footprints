# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements. See the NOTICE file distributed with this
# work for additional information regarding copyright ownership. The ASF
# licenses this file to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.

module Buildr
  module JaCoCo
    class << self
      VERSION = '0.5.10.201208310627'

      def version
        @version || Buildr.settings.build['jacoco'] || VERSION
      end

      def version=(value)
        @version = value
      end

      # The specs for agent
      def agent_spec
        ["org.jacoco:org.jacoco.agent:jar:runtime:#{version}"]
      end
    end

    class Config

      attr_writer :enabled

      def enabled?
        @enabled.nil? ? true : @enabled
      end

      attr_writer :destfile

      def destfile
        @destfile || "#{self.report_dir}/jacoco.cov"
      end

      attr_writer :output

      def output
        @output || 'file'
      end

      attr_accessor :sessionid
      attr_accessor :address
      attr_accessor :port
      attr_accessor :classdumpdir
      attr_accessor :dumponexit
      attr_accessor :append
      attr_accessor :exclclassloader

      def includes
        @includes ||= []
      end

      def excludes
        @excludes ||= []
      end

      def html_enabled?
        File.exist?(self.style_file)
      end

      attr_writer :config_directory

      def config_directory
        @config_directory || project._(:source, :main, :etc, :jacoco)
      end

      attr_writer :report_dir

      def report_dir
        @report_dir || project._(:reports, :jacoco)
      end

      attr_writer :fail_on_error

      def fail_on_error?
        @fail_on_error.nil? ? false : @fail_on_error
      end

      attr_writer :xml_output_file

      def xml_output_file
        @xml_output_file || "#{self.report_dir}/jacoco.xml"
      end

      attr_writer :html_output_file

      def html_output_file
        @html_output_file || "#{self.report_dir}/jacoco.html"
      end

      attr_writer :style_file

      def style_file
        @style_file || "#{self.config_directory}/jacoco-report.xsl"
      end

      protected

      def initialize(project)
        @project = project
      end

      attr_reader :project

    end

    module ProjectExtension
      include Extension

      def jacoco
        @jacoco ||= Buildr::JaCoCo::Config.new(project)
      end

      after_define do |project|
        unless project.test.compile.target.nil? || !project.jacoco.enabled?
          namespace 'jacoco' do
            project.test.setup do
              agent_jar = Buildr.artifacts(Buildr::JaCoCo.agent_spec).each(&:invoke).map(&:to_s)
              options = []
              ["destfile",
               "append",
               "exclclassloader",
               "sessionid",
               "dumponexit",
               "output",
               "address",
               "port",
               "classdumpdir"].each do |option|
                value = project.jacoco.send(option.to_sym)
                options << "#{option}=#{value}" unless value.nil?
              end
              options << "includes=#{project.jacoco.includes.join(':')}" unless project.jacoco.includes.empty?
              options << "excludes=#{project.jacoco.excludes.join(':')}" unless project.jacoco.excludes.empty?

              agent_config = "-javaagent:#{agent_jar}=#{options.join(',')}"
              project.test.options[:java_args] = (project.test.options[:java_args] || []) + [agent_config]
            end
          end
        end
      end
    end
  end
end

class Buildr::Project
  include Buildr::JaCoCo::ProjectExtension
end
