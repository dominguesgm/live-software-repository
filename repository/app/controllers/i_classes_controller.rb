class IClassesController < ApplicationController

    def create
        
        params.delete("controller")
        params.delete("action")

        if params.key?("projectName") && params.key?("packageName")

            proj = Project.find_by(project_name: params[:projectName])
            pack = Package.find_by(package_name: params[:packageName], project_id: proj[:id])

            if IClass.exists?(class_name: params[:className], package_id: pack[:id])
                oldClass = IClass.find_by(class_name: params[:className], package_id: pack[:id])
                oldClass.destroy
            end

            @res_cls = IClass.new( { :class_name => params[:className],
                :qualified_name => params[:qualifiedName],
                :method_count => params[:methodCount],
                :attribute_count => params[:attributeCount],
                :lines_of_code => params[:linesOfCode],
                :class_hash => params[:hash],
                :package_id => pack[:id] } )

            for attrib in params[:attributes]
                @res_attrib = @res_cls.class_attributes.build({ :attribute_name => attrib[:attributeName],
                    :attribute_type => attrib[:type]})

            end

            for meth in params[:methods]
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

            classes = []
            classes << @res_cls

            @result = IClass.import classes, recursive: true

            ActionCable.server.broadcast("control_channel", {"fetch_structure" => "class",
                "operation" => "change",
                "package_id" => pack[:id], "project_id" => proj[:id],
                "class_id" => @result[:ids][0]}.to_json())
            
            render :json => @result, :status => :created
        else
            render :json => {}, :status => :bad_request
        end
    end

    def show
        render :json => IClass.find(params[:id])
    end

    def index
        @i_classes = IClass.all 

        render :json => @i_classes, :code => :ok
    end

    def destroy

        proj = Project.find_by(project_name: params[:projectName])
        pack = Package.find_by(package_name: params[:packageName], project_id: proj[:id])
        oldClass = IClass.find_by(class_name: params[:className], package_id: pack[:id])
        class_id = oldClass[:id]
        oldClass.destroy

        ActionCable.server.broadcast("control_channel", {"fetch_structure" => "class",
            "operation" => "delete",
            "package_id" => pack[:id], "project_id" => proj[:id],
            "class_id" => class_id}.to_json())

        render :json => {}, :code => :no_content
        
    end

end
