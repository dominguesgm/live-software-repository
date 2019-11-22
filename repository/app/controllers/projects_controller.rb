class ProjectsController < ApplicationController
    
    def create
        
        params.delete("controller")
        params.delete("action")

        if Project.exists?(project_name: params[:projectName])
            Project.find_by(project_name: params[:projectName]).destroy
        end

        if params.key?("packages")
            ActiveRecord::Base.logger.silence do
                createDeep(params)
            end
        else
            ActiveRecord::Base.logger.silence do
                createShallow(params)
            end
        end
    end

    def show
        if params.key?("deep") && params[:deep] == "true"
            deepRender()
        else
            render :json => Project.find(params[:id])
        end
    end

    def index
        if params.key?("name")
            @projects = Project.where(project_name: params[:name])
        else
            @projects = Project.all 
        end

        render :json => @projects, :code => :ok
    end

    def destroy

        Project.find(params[:id]).destroy

        render :json => {}, :code => :no_content

    end

    def clear

        Project.destroy_all

        render :json => {}, :code => :no_content

    end

    protected

    def createShallow(proj)
        @project = Project.new( { :project_name => params[:projectName],
            :num_packages => params[:numPackages] } )
        
        if @project.save
            render :json => @project, :status => :created
        else
            render :json => {}, :status => :unprocessable_entity
        end
    end

    protected

    def createDeep(proj)

        @projects = []

        @project = Project.new( { :project_name => proj[:projectName],
            :num_packages => proj[:numPackages] } )

        for pack in proj[:packages]
            @res_pack = @project.packages.build({ :package_name => pack[:packageName],
                :class_count => pack[:classCount], 
                :package_path => pack[:packagePath],
                :has_subpackages => pack[:hasSubpackages]})

            for cls in pack[:classes]
                @res_cls = @res_pack.i_classes.build({ :class_name => cls[:className],
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
        end

        @projects << @project

        @result = Project.import @projects, recursive: true

        ActionCable.server.broadcast("control_channel", {"fetch_structure" => "project",
            "operation" => "change",
            "project_id" => @result[:ids][0]}.to_json())
        
        render :json => @result, :status => :created

    end

    def deepRender()

        @export_data = {:allProjectData =>  [Project.find(params[:id])]}
    
        render :json => @export_data.to_json(:include => 
            {:packages => {:include => 
                {:i_classes => {:include => 
                    [:class_attributes, 
                        {:i_methods => 
                            {:include => 
                                [:arguments, :method_invocations => {:except => [:created_at, :updated_at]}],
                            :except => [:created_at, :updated_at]
                            }
                        }
                    ],
                    :except => [:created_at, :updated_at]
                }
                },
                :except => [:created_at, :updated_at]
            }
        }, :except => [:created_at, :updated_at])
    end


end


