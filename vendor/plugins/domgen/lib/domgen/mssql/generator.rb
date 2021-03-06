#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

module Domgen
  module Generator
    module Mssql
      TEMPLATE_DIRECTORY = "#{File.dirname(__FILE__)}/templates"
      FACETS = [:mssql]
    end
  end
end
Domgen.template_set(:mssql => [:sql_dbt_config]) do |template_set|
  template_set.template(Domgen::Generator::Mssql::FACETS,
                        :repository,
                        "#{Domgen::Generator::Mssql::TEMPLATE_DIRECTORY}/mssql_database_setup.sql.erb",
                        'db-hooks/pre/database_setup.sql')
  template_set.template(Domgen::Generator::Mssql::FACETS,
                        :data_module,
                        "#{Domgen::Generator::Mssql::TEMPLATE_DIRECTORY}/mssql_ddl.sql.erb",
                        '#{data_module.name}/schema.sql',
                        [::Domgen::Ruby::MssqlHelper])
  template_set.template(Domgen::Generator::Mssql::FACETS,
                        :data_module,
                        "#{Domgen::Generator::Mssql::TEMPLATE_DIRECTORY}/mssql_finalize.sql.erb",
                        '#{data_module.name}/finalize/schema_finalize.sql',
                        [::Domgen::Ruby::MssqlHelper])
end
