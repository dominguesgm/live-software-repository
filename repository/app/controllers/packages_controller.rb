class PackagesController < ApplicationController

    def create
        
        params.delete("controller")
        params.delete("action")

        if params.key?("projectName")

            proj = Project.find_by(project_name: params[:projectName])

            if Package.exists?(package_name: params[:packageName], project_id: proj[:id])
                Package.find_by(package_name: params[:packageName], project_id: proj[:id]).destroy
            end
                    
            res_pack = Package.new( { :package_name => params[:packageName],
                :class_count => params[:classCount], 
                :has_subpackages => params[:hasSubpackages],
                :package_path => params[:packagePath],
                :project_id => proj[:id] } )

            for cls in params[:classes]
                @res_cls = res_pack.i_classes.build({ :class_name => cls[:className],
                    :qualified_name => cls[:qualifiedName],
                    :method_count => cls[:methodCount],
                    :attribute_count => cls[:attributeCount],
                    :lines_of_code => cls[:linesOfCode],
                    :class_hash => cls[:hash]})

                for attrib in cls[:attributes]
                    @res_attrib = @res_cls.class_attributes.build({ :attribute_name => attrib[:attributeName],
                        :attribute_type => attrib[:type]})

                end

                for meth in cls[:methods]
                    @res_meth = @res_cls.i_methods.build( { :method_name => meth[:methodName],
                        :start_of_method => meth[:startOfMethod],
                        :length_of_method => meth[:lengthOfMethod],
                        :lines_of_code => meth[:linesOfCode],
                        :return_type => meth[:returnType],
                        :argument_count => meth[:argumentCount],
                        :key => meth[:key] })

                    for arg in meth[:arguments]
                        @res_meth.arguments.build({ :argument_name => arg[:argumentName],
                            :argument_type => arg[:type] })
                    end

                    for invoc in meth[:methodInvocations]
                        @res_meth.method_invocations.build({ :invocation => invoc })
                    end
                end
            end

            packages = []
            packages << res_pack

            @result = Package.import packages, recursive: true

            ActionCable.server.broadcast("control_channel", {"fetch_structure" => "package",
                "operation" => "change",
                "package_id" => @result[:ids][0], "project_id" => proj[:id]}.to_json())

            render :json => @result, :status => :created
        else
            render :json => {}, :status => :bad_request
        end
    end

    def show
        render :json => Package.find(params[:id])
    end

    def index
        @packages = Package.all 

        render :json => @packages, :code => :ok
    end

    def destroy

        proj = Project.find_by(project_name: params[:projectName])
        pack = Package.find_by(package_name: params[:packageName], project_id: proj[:id])
        pack_id = pack[:id]
        pack.destroy

        ActionCable.server.broadcast("control_channel", {"fetch_structure" => "class",
            "operation" => "delete",
            "package_id" => pack_iod, "project_id" => proj[:id]}.to_json())

        render :json => {}, :code => :no_content
        
    end
end
