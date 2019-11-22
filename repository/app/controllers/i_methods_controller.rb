class IMethodsController < ApplicationController

    def create
        
        params.delete("controller")
        params.delete("action")
                
        @i_method = IMethod.new( { :method_name => params[:methodName],
            :start_of_method => params[:startOfMethod],
            :length_of_method => params[:lengthOfMethod],
            :lines_of_code => params[:linesOfCode],
            :return_type => params[:returnType],
            :argument_count => params[:argumentCount],
            :key => params[:key],
            :i_class_id => params[:classId] } )
        
        if @i_method.save
            render :json => @i_method, :status => :created
        else
            render :json => {}, :status => :unprocessable_entity
        end
    end

    def show
        render :json => IMethod.find(params[:id])
    end

    def index
        @i_methods = IMethod.all 

        render :json => @i_methods, :code => :ok
    end

end
