class MethodInvocationsController < ApplicationController

    def create
        
        params.delete("controller")
        params.delete("action")
                
        @method_invocation = MethodInvocation.new( { :invocation => params[:invocation],
            :i_method_id => params[:methodId] } )
        
        if @method_invocation.save
            render :json => @method_invocation, :status => :created
        else
            puts @method_invocation.errors.full_messages
            render :json => {}, :status => :unprocessable_entity
        end
    end

    def show
        render :json => MethodInvocation.find(params[:id])
    end

    def index
        @method_invocations = MethodInvocation.all 

        render :json => @method_invocations, :code => :ok
    end

end
