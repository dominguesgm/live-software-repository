class ArgumentsController < ApplicationController

    def create      
        
        params.delete("controller")
        params.delete("action")
                
        @argument = Argument.new( { :argument_name => params[:argumentName],
            :argument_type => params[:type],
            :i_method_id => params[:methodId] } )
        
        if @argument.save
            render :json => @argument, :status => :created
        else
            render :json => {}, :status => :unprocessable_entity
        end
    end

    def show
        render :json => Argument.find(params[:id])
    end

    def index
        @arguments = Argument.all 

        render :json => @arguments, :code => :ok
    end

end
